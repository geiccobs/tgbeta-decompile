package com.google.firebase.messaging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.datatransport.Encoding;
import com.google.android.datatransport.Event;
import com.google.android.datatransport.Transport;
import com.google.android.datatransport.TransportFactory;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.reporting.MessagingClientEvent;
import com.google.firebase.messaging.reporting.MessagingClientEventExtension;
import com.huawei.hms.push.constant.RemoteMessageConst;
import java.util.concurrent.ExecutionException;
import org.telegram.tgnet.ConnectionsManager;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes.dex */
public class MessagingAnalytics {
    static boolean deliveryMetricsExportToBigQueryEnabled() {
        ApplicationInfo applicationInfo;
        Bundle bundle;
        try {
            FirebaseApp.getInstance();
            Context applicationContext = FirebaseApp.getInstance().getApplicationContext();
            SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("com.google.firebase.messaging", 0);
            if (sharedPreferences.contains("export_to_big_query")) {
                return sharedPreferences.getBoolean("export_to_big_query", false);
            }
            try {
                PackageManager packageManager = applicationContext.getPackageManager();
                if (packageManager != null && (applicationInfo = packageManager.getApplicationInfo(applicationContext.getPackageName(), ConnectionsManager.RequestFlagNeedQuickAck)) != null && (bundle = applicationInfo.metaData) != null && bundle.containsKey("delivery_metrics_exported_to_big_query_enabled")) {
                    return applicationInfo.metaData.getBoolean("delivery_metrics_exported_to_big_query_enabled", false);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            return false;
        } catch (IllegalStateException unused2) {
            Log.i("FirebaseMessaging", "FirebaseApp has not being initialized. Device might be in direct boot mode. Skip exporting delivery metrics to Big Query");
            return false;
        }
    }

    static MessagingClientEvent eventToProto(MessagingClientEvent.Event event, Intent intent) {
        if (intent == null) {
            return null;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            extras = Bundle.EMPTY;
        }
        MessagingClientEvent.Builder newBuilder = MessagingClientEvent.newBuilder();
        newBuilder.setTtl(getTtl(extras));
        newBuilder.setEvent(event);
        newBuilder.setInstanceId(getInstanceId(extras));
        newBuilder.setPackageName(getPackageName());
        newBuilder.setSdkPlatform(MessagingClientEvent.SDKPlatform.ANDROID);
        newBuilder.setMessageType(getMessageTypeForFirelog(extras));
        String messageId = getMessageId(extras);
        if (messageId != null) {
            newBuilder.setMessageId(messageId);
        }
        String topic = getTopic(extras);
        if (topic != null) {
            newBuilder.setTopic(topic);
        }
        String collapseKey = getCollapseKey(extras);
        if (collapseKey != null) {
            newBuilder.setCollapseKey(collapseKey);
        }
        String messageLabel = getMessageLabel(extras);
        if (messageLabel != null) {
            newBuilder.setAnalyticsLabel(messageLabel);
        }
        String composerLabel = getComposerLabel(extras);
        if (composerLabel != null) {
            newBuilder.setComposerLabel(composerLabel);
        }
        long projectNumber = getProjectNumber(extras);
        if (projectNumber > 0) {
            newBuilder.setProjectNumber(projectNumber);
        }
        return newBuilder.build();
    }

    static String getCollapseKey(Bundle bundle) {
        return bundle.getString("collapse_key");
    }

    static String getComposerId(Bundle bundle) {
        return bundle.getString("google.c.a.c_id");
    }

    static String getComposerLabel(Bundle bundle) {
        return bundle.getString("google.c.a.c_l");
    }

    static String getInstanceId(Bundle bundle) {
        String string = bundle.getString("google.to");
        if (!TextUtils.isEmpty(string)) {
            return string;
        }
        try {
            return (String) Tasks.await(FirebaseInstallations.getInstance(FirebaseApp.getInstance()).getId());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    static String getMessageChannel(Bundle bundle) {
        return bundle.getString("google.c.a.m_c");
    }

    static String getMessageId(Bundle bundle) {
        String string = bundle.getString("google.message_id");
        return string == null ? bundle.getString("message_id") : string;
    }

    static String getMessageLabel(Bundle bundle) {
        return bundle.getString("google.c.a.m_l");
    }

    static String getMessageTime(Bundle bundle) {
        return bundle.getString("google.c.a.ts");
    }

    static MessagingClientEvent.MessageType getMessageTypeForFirelog(Bundle bundle) {
        if (bundle == null || !NotificationParams.isNotification(bundle)) {
            return MessagingClientEvent.MessageType.DATA_MESSAGE;
        }
        return MessagingClientEvent.MessageType.DISPLAY_NOTIFICATION;
    }

    static String getMessageTypeForScion(Bundle bundle) {
        return true != NotificationParams.isNotification(bundle) ? RemoteMessageConst.DATA : "display";
    }

    static String getPackageName() {
        return FirebaseApp.getInstance().getApplicationContext().getPackageName();
    }

    static long getProjectNumber(Bundle bundle) {
        if (bundle.containsKey("google.c.sender.id")) {
            try {
                return Long.parseLong(bundle.getString("google.c.sender.id"));
            } catch (NumberFormatException e) {
                Log.w("FirebaseMessaging", "error parsing project number", e);
            }
        }
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        String gcmSenderId = firebaseApp.getOptions().getGcmSenderId();
        if (gcmSenderId != null) {
            try {
                return Long.parseLong(gcmSenderId);
            } catch (NumberFormatException e2) {
                Log.w("FirebaseMessaging", "error parsing sender ID", e2);
            }
        }
        String applicationId = firebaseApp.getOptions().getApplicationId();
        if (!applicationId.startsWith("1:")) {
            try {
                return Long.parseLong(applicationId);
            } catch (NumberFormatException e3) {
                Log.w("FirebaseMessaging", "error parsing app ID", e3);
            }
        } else {
            String[] split = applicationId.split(":");
            if (split.length < 2) {
                return 0L;
            }
            String str = split[1];
            if (str.isEmpty()) {
                return 0L;
            }
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e4) {
                Log.w("FirebaseMessaging", "error parsing app ID", e4);
            }
        }
        return 0L;
    }

    static String getTopic(Bundle bundle) {
        String string = bundle.getString(RemoteMessageConst.FROM);
        if (string == null || !string.startsWith("/topics/")) {
            return null;
        }
        return string;
    }

    static int getTtl(Bundle bundle) {
        Object obj = bundle.get("google.ttl");
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (!(obj instanceof String)) {
            return 0;
        }
        try {
            return Integer.parseInt((String) obj);
        } catch (NumberFormatException unused) {
            String valueOf = String.valueOf(obj);
            StringBuilder sb = new StringBuilder(valueOf.length() + 13);
            sb.append("Invalid TTL: ");
            sb.append(valueOf);
            Log.w("FirebaseMessaging", sb.toString());
            return 0;
        }
    }

    static String getUseDeviceTime(Bundle bundle) {
        if (bundle.containsKey("google.c.a.udt")) {
            return bundle.getString("google.c.a.udt");
        }
        return null;
    }

    private static boolean isDirectBootMessage(Intent intent) {
        return FirebaseMessagingService.ACTION_DIRECT_BOOT_REMOTE_INTENT.equals(intent.getAction());
    }

    public static void logNotificationDismiss(Intent intent) {
        logToScion("_nd", intent.getExtras());
    }

    public static void logNotificationForeground(Intent intent) {
        logToScion("_nf", intent.getExtras());
    }

    public static void logNotificationOpen(Bundle bundle) {
        setUserPropertyIfRequired(bundle);
        logToScion("_no", bundle);
    }

    public static void logNotificationReceived(Intent intent) {
        if (shouldUploadScionMetrics(intent)) {
            logToScion("_nr", intent.getExtras());
        }
        if (shouldUploadFirelogAnalytics(intent)) {
            logToFirelog(MessagingClientEvent.Event.MESSAGE_DELIVERED, intent, FirebaseMessaging.getTransportFactory());
        }
    }

    private static void logToFirelog(MessagingClientEvent.Event event, Intent intent, TransportFactory transportFactory) {
        if (transportFactory == null) {
            Log.e("FirebaseMessaging", "TransportFactory is null. Skip exporting message delivery metrics to Big Query");
            return;
        }
        MessagingClientEvent eventToProto = eventToProto(event, intent);
        if (eventToProto == null) {
            return;
        }
        try {
            Transport transport = transportFactory.getTransport("FCM_CLIENT_EVENT_LOGGING", MessagingClientEventExtension.class, Encoding.of("proto"), MessagingAnalytics$$Lambda$0.$instance);
            MessagingClientEventExtension.Builder newBuilder = MessagingClientEventExtension.newBuilder();
            newBuilder.setMessagingClientEvent(eventToProto);
            transport.send(Event.ofTelemetry(newBuilder.build()));
        } catch (RuntimeException e) {
            Log.w("FirebaseMessaging", "Failed to send big query analytics payload.", e);
        }
    }

    static void logToScion(String str, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        Bundle bundle2 = new Bundle();
        String composerId = getComposerId(bundle);
        if (composerId != null) {
            bundle2.putString("_nmid", composerId);
        }
        String composerLabel = getComposerLabel(bundle);
        if (composerLabel != null) {
            bundle2.putString("_nmn", composerLabel);
        }
        String messageLabel = getMessageLabel(bundle);
        if (!TextUtils.isEmpty(messageLabel)) {
            bundle2.putString("label", messageLabel);
        }
        String messageChannel = getMessageChannel(bundle);
        if (!TextUtils.isEmpty(messageChannel)) {
            bundle2.putString("message_channel", messageChannel);
        }
        String topic = getTopic(bundle);
        if (topic != null) {
            bundle2.putString("_nt", topic);
        }
        String messageTime = getMessageTime(bundle);
        if (messageTime != null) {
            try {
                bundle2.putInt("_nmt", Integer.parseInt(messageTime));
            } catch (NumberFormatException e) {
                Log.w("FirebaseMessaging", "Error while parsing timestamp in GCM event", e);
            }
        }
        String useDeviceTime = getUseDeviceTime(bundle);
        if (useDeviceTime != null) {
            try {
                bundle2.putInt("_ndt", Integer.parseInt(useDeviceTime));
            } catch (NumberFormatException e2) {
                Log.w("FirebaseMessaging", "Error while parsing use_device_time in GCM event", e2);
            }
        }
        String messageTypeForScion = getMessageTypeForScion(bundle);
        if ("_nr".equals(str) || "_nf".equals(str)) {
            bundle2.putString("_nmc", messageTypeForScion);
        }
        if (Log.isLoggable("FirebaseMessaging", 3)) {
            String valueOf = String.valueOf(bundle2);
            StringBuilder sb = new StringBuilder(str.length() + 37 + valueOf.length());
            sb.append("Logging to scion event=");
            sb.append(str);
            sb.append(" scionPayload=");
            sb.append(valueOf);
            Log.d("FirebaseMessaging", sb.toString());
        }
        AnalyticsConnector analyticsConnector = (AnalyticsConnector) FirebaseApp.getInstance().get(AnalyticsConnector.class);
        if (analyticsConnector != null) {
            analyticsConnector.logEvent("fcm", str, bundle2);
        } else {
            Log.w("FirebaseMessaging", "Unable to log event: analytics library is missing");
        }
    }

    private static void setUserPropertyIfRequired(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if ("1".equals(bundle.getString("google.c.a.tc"))) {
            AnalyticsConnector analyticsConnector = (AnalyticsConnector) FirebaseApp.getInstance().get(AnalyticsConnector.class);
            if (Log.isLoggable("FirebaseMessaging", 3)) {
                Log.d("FirebaseMessaging", "Received event with track-conversion=true. Setting user property and reengagement event");
            }
            if (analyticsConnector != null) {
                String string = bundle.getString("google.c.a.c_id");
                analyticsConnector.setUserProperty("fcm", "_ln", string);
                Bundle bundle2 = new Bundle();
                bundle2.putString("source", "Firebase");
                bundle2.putString("medium", RemoteMessageConst.NOTIFICATION);
                bundle2.putString("campaign", string);
                analyticsConnector.logEvent("fcm", "_cmp", bundle2);
                return;
            }
            Log.w("FirebaseMessaging", "Unable to set user property for conversion tracking:  analytics library is missing");
        } else if (!Log.isLoggable("FirebaseMessaging", 3)) {
        } else {
            Log.d("FirebaseMessaging", "Received event with track-conversion=false. Do not set user property");
        }
    }

    public static boolean shouldUploadFirelogAnalytics(Intent intent) {
        if (intent == null || isDirectBootMessage(intent)) {
            return false;
        }
        return deliveryMetricsExportToBigQueryEnabled();
    }

    public static boolean shouldUploadScionMetrics(Intent intent) {
        if (intent == null || isDirectBootMessage(intent)) {
            return false;
        }
        return shouldUploadScionMetrics(intent.getExtras());
    }

    public static boolean shouldUploadScionMetrics(Bundle bundle) {
        if (bundle == null) {
            return false;
        }
        return "1".equals(bundle.getString("google.c.a.e"));
    }
}
