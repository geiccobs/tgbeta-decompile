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
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.reporting.MessagingClientEvent;
import com.google.firebase.messaging.reporting.MessagingClientEventExtension;
import java.util.concurrent.ExecutionException;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
public class MessagingAnalytics {
    public static boolean deliveryMetricsExportToBigQueryEnabled() {
        ApplicationInfo applicationInfo;
        try {
            FirebaseApp.getInstance();
            Context applicationContext = FirebaseApp.getInstance().getApplicationContext();
            SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("com.google.firebase.messaging", 0);
            if (sharedPreferences.contains("export_to_big_query")) {
                return sharedPreferences.getBoolean("export_to_big_query", false);
            }
            try {
                PackageManager packageManager = applicationContext.getPackageManager();
                if (packageManager != null && (applicationInfo = packageManager.getApplicationInfo(applicationContext.getPackageName(), 128)) != null && applicationInfo.metaData != null && applicationInfo.metaData.containsKey("delivery_metrics_exported_to_big_query_enabled")) {
                    return applicationInfo.metaData.getBoolean("delivery_metrics_exported_to_big_query_enabled", false);
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
            return false;
        } catch (IllegalStateException e2) {
            Log.i(Constants.TAG, "FirebaseApp has not being initialized. Device might be in direct boot mode. Skip exporting delivery metrics to Big Query");
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
        return bundle.getString(Constants.MessagePayloadKeys.COLLAPSE_KEY);
    }

    static String getComposerId(Bundle bundle) {
        return bundle.getString(Constants.AnalyticsKeys.COMPOSER_ID);
    }

    static String getComposerLabel(Bundle bundle) {
        return bundle.getString(Constants.AnalyticsKeys.COMPOSER_LABEL);
    }

    static String getInstanceId(Bundle bundle) {
        String string = bundle.getString(Constants.MessagePayloadKeys.TO);
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
        return bundle.getString(Constants.AnalyticsKeys.MESSAGE_CHANNEL);
    }

    static String getMessageId(Bundle bundle) {
        String string = bundle.getString(Constants.MessagePayloadKeys.MSGID);
        return string == null ? bundle.getString(Constants.MessagePayloadKeys.MSGID_SERVER) : string;
    }

    static String getMessageLabel(Bundle bundle) {
        return bundle.getString(Constants.AnalyticsKeys.MESSAGE_LABEL);
    }

    static String getMessageTime(Bundle bundle) {
        return bundle.getString(Constants.AnalyticsKeys.MESSAGE_TIMESTAMP);
    }

    static MessagingClientEvent.MessageType getMessageTypeForFirelog(Bundle bundle) {
        if (bundle == null || !NotificationParams.isNotification(bundle)) {
            return MessagingClientEvent.MessageType.DATA_MESSAGE;
        }
        return MessagingClientEvent.MessageType.DISPLAY_NOTIFICATION;
    }

    static String getMessageTypeForScion(Bundle bundle) {
        return true != NotificationParams.isNotification(bundle) ? "data" : Constants.ScionAnalytics.MessageType.DISPLAY_NOTIFICATION;
    }

    static String getPackageName() {
        return FirebaseApp.getInstance().getApplicationContext().getPackageName();
    }

    static long getProjectNumber(Bundle bundle) {
        if (bundle.containsKey(Constants.MessagePayloadKeys.SENDER_ID)) {
            try {
                return Long.parseLong(bundle.getString(Constants.MessagePayloadKeys.SENDER_ID));
            } catch (NumberFormatException e) {
                Log.w(Constants.TAG, "error parsing project number", e);
            }
        }
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        String gcmSenderId = firebaseApp.getOptions().getGcmSenderId();
        if (gcmSenderId != null) {
            try {
                return Long.parseLong(gcmSenderId);
            } catch (NumberFormatException e2) {
                Log.w(Constants.TAG, "error parsing sender ID", e2);
            }
        }
        String applicationId = firebaseApp.getOptions().getApplicationId();
        if (!applicationId.startsWith("1:")) {
            try {
                return Long.parseLong(applicationId);
            } catch (NumberFormatException e3) {
                Log.w(Constants.TAG, "error parsing app ID", e3);
            }
        } else {
            String[] split = applicationId.split(com.microsoft.appcenter.Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
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
                Log.w(Constants.TAG, "error parsing app ID", e4);
            }
        }
        return 0L;
    }

    static String getTopic(Bundle bundle) {
        String string = bundle.getString(Constants.MessagePayloadKeys.FROM);
        if (string == null || !string.startsWith("/topics/")) {
            return null;
        }
        return string;
    }

    static int getTtl(Bundle bundle) {
        Object obj = bundle.get(Constants.MessagePayloadKeys.TTL);
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                String valueOf = String.valueOf(obj);
                StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 13);
                sb.append("Invalid TTL: ");
                sb.append(valueOf);
                Log.w(Constants.TAG, sb.toString());
                return 0;
            }
        }
        return 0;
    }

    static String getUseDeviceTime(Bundle bundle) {
        if (bundle.containsKey(Constants.AnalyticsKeys.MESSAGE_USE_DEVICE_TIME)) {
            return bundle.getString(Constants.AnalyticsKeys.MESSAGE_USE_DEVICE_TIME);
        }
        return null;
    }

    private static boolean isDirectBootMessage(Intent intent) {
        return FirebaseMessagingService.ACTION_DIRECT_BOOT_REMOTE_INTENT.equals(intent.getAction());
    }

    public static void logNotificationDismiss(Intent intent) {
        logToScion(Constants.ScionAnalytics.EVENT_NOTIFICATION_DISMISS, intent.getExtras());
    }

    public static void logNotificationForeground(Intent intent) {
        logToScion(Constants.ScionAnalytics.EVENT_NOTIFICATION_FOREGROUND, intent.getExtras());
    }

    public static void logNotificationOpen(Bundle extras) {
        setUserPropertyIfRequired(extras);
        logToScion(Constants.ScionAnalytics.EVENT_NOTIFICATION_OPEN, extras);
    }

    public static void logNotificationReceived(Intent intent) {
        if (shouldUploadScionMetrics(intent)) {
            logToScion(Constants.ScionAnalytics.EVENT_NOTIFICATION_RECEIVE, intent.getExtras());
        }
        if (shouldUploadFirelogAnalytics(intent)) {
            logToFirelog(MessagingClientEvent.Event.MESSAGE_DELIVERED, intent, FirebaseMessaging.getTransportFactory());
        }
    }

    private static void logToFirelog(MessagingClientEvent.Event event, Intent intent, TransportFactory transportFactory) {
        if (transportFactory == null) {
            Log.e(Constants.TAG, "TransportFactory is null. Skip exporting message delivery metrics to Big Query");
            return;
        }
        MessagingClientEvent eventToProto = eventToProto(event, intent);
        if (eventToProto == null) {
            return;
        }
        try {
            Transport transport = transportFactory.getTransport(Constants.FirelogAnalytics.FCM_LOG_SOURCE, MessagingClientEventExtension.class, Encoding.of("proto"), MessagingAnalytics$$Lambda$0.$instance);
            MessagingClientEventExtension.Builder newBuilder = MessagingClientEventExtension.newBuilder();
            newBuilder.setMessagingClientEvent(eventToProto);
            transport.send(Event.ofTelemetry(newBuilder.build()));
        } catch (RuntimeException e) {
            Log.w(Constants.TAG, "Failed to send big query analytics payload.", e);
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
            bundle2.putString(Constants.ScionAnalytics.PARAM_MESSAGE_NAME, composerLabel);
        }
        String messageLabel = getMessageLabel(bundle);
        if (!TextUtils.isEmpty(messageLabel)) {
            bundle2.putString(Constants.ScionAnalytics.PARAM_LABEL, messageLabel);
        }
        String messageChannel = getMessageChannel(bundle);
        if (!TextUtils.isEmpty(messageChannel)) {
            bundle2.putString(Constants.ScionAnalytics.PARAM_MESSAGE_CHANNEL, messageChannel);
        }
        String topic = getTopic(bundle);
        if (topic != null) {
            bundle2.putString(Constants.ScionAnalytics.PARAM_TOPIC, topic);
        }
        String messageTime = getMessageTime(bundle);
        if (messageTime != null) {
            try {
                bundle2.putInt(Constants.ScionAnalytics.PARAM_MESSAGE_TIME, Integer.parseInt(messageTime));
            } catch (NumberFormatException e) {
                Log.w(Constants.TAG, "Error while parsing timestamp in GCM event", e);
            }
        }
        String useDeviceTime = getUseDeviceTime(bundle);
        if (useDeviceTime != null) {
            try {
                bundle2.putInt(Constants.ScionAnalytics.PARAM_MESSAGE_DEVICE_TIME, Integer.parseInt(useDeviceTime));
            } catch (NumberFormatException e2) {
                Log.w(Constants.TAG, "Error while parsing use_device_time in GCM event", e2);
            }
        }
        String messageTypeForScion = getMessageTypeForScion(bundle);
        if (Constants.ScionAnalytics.EVENT_NOTIFICATION_RECEIVE.equals(str) || Constants.ScionAnalytics.EVENT_NOTIFICATION_FOREGROUND.equals(str)) {
            bundle2.putString(Constants.ScionAnalytics.PARAM_MESSAGE_TYPE, messageTypeForScion);
        }
        if (Log.isLoggable(Constants.TAG, 3)) {
            String valueOf = String.valueOf(bundle2);
            StringBuilder sb = new StringBuilder(str.length() + 37 + String.valueOf(valueOf).length());
            sb.append("Logging to scion event=");
            sb.append(str);
            sb.append(" scionPayload=");
            sb.append(valueOf);
            Log.d(Constants.TAG, sb.toString());
        }
        AnalyticsConnector analyticsConnector = (AnalyticsConnector) FirebaseApp.getInstance().get(AnalyticsConnector.class);
        if (analyticsConnector != null) {
            analyticsConnector.logEvent(Constants.ScionAnalytics.ORIGIN_FCM, str, bundle2);
        } else {
            Log.w(Constants.TAG, "Unable to log event: analytics library is missing");
        }
    }

    public static void setDeliveryMetricsExportToBigQuery(boolean z) {
        FirebaseApp.getInstance().getApplicationContext().getSharedPreferences("com.google.firebase.messaging", 0).edit().putBoolean("export_to_big_query", z).apply();
    }

    private static void setUserPropertyIfRequired(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(bundle.getString(Constants.AnalyticsKeys.TRACK_CONVERSIONS))) {
            AnalyticsConnector analyticsConnector = (AnalyticsConnector) FirebaseApp.getInstance().get(AnalyticsConnector.class);
            if (Log.isLoggable(Constants.TAG, 3)) {
                Log.d(Constants.TAG, "Received event with track-conversion=true. Setting user property and reengagement event");
            }
            if (analyticsConnector != null) {
                String string = bundle.getString(Constants.AnalyticsKeys.COMPOSER_ID);
                analyticsConnector.setUserProperty(Constants.ScionAnalytics.ORIGIN_FCM, Constants.ScionAnalytics.USER_PROPERTY_FIREBASE_LAST_NOTIFICATION, string);
                Bundle bundle2 = new Bundle();
                bundle2.putString(Constants.ScionAnalytics.PARAM_SOURCE, "Firebase");
                bundle2.putString(Constants.ScionAnalytics.PARAM_MEDIUM, "notification");
                bundle2.putString(Constants.ScionAnalytics.PARAM_CAMPAIGN, string);
                analyticsConnector.logEvent(Constants.ScionAnalytics.ORIGIN_FCM, Constants.ScionAnalytics.EVENT_FIREBASE_CAMPAIGN, bundle2);
                return;
            }
            Log.w(Constants.TAG, "Unable to set user property for conversion tracking:  analytics library is missing");
        } else if (!Log.isLoggable(Constants.TAG, 3)) {
        } else {
            Log.d(Constants.TAG, "Received event with track-conversion=false. Do not set user property");
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

    public static boolean shouldUploadScionMetrics(Bundle extras) {
        if (extras == null) {
            return false;
        }
        return IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(extras.getString(Constants.AnalyticsKeys.ENABLED));
    }
}
