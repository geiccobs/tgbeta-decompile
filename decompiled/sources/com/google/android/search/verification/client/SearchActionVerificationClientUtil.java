package com.google.android.search.verification.client;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import java.util.Set;
/* loaded from: classes3.dex */
public abstract class SearchActionVerificationClientUtil {
    public static final String ANDROID_AUTO_PACKAGE = "com.google.android.gearhead";
    public static final String ANDROID_WEAR_PACKAGE = "com.google.android.wearable.app";
    public static final String ASSISTANT_GO_PACKAGE = "com.google.android.apps.assistant";
    private static final String AUDIO_CONTENT_URI_KEY = "android.preview.support.NotificationExtras.AUDIO_CONTENT_URI_KEY";
    private static final String CONTENT_ID_KEY = "android.preview.support.NotificationExtras.CONTENT_ID_KEY";
    private static final String INPUT_REQUIRES_AUDIO_INPUT_KEY = "wear.a.ALLOWS_DATA";
    public static final String SEARCH_APP_PACKAGE = "com.google.android.googlequicksearchbox";
    private static final String TAG = "SAVerificationClientU";
    public static final String TESTING_APP_PACKAGE = "com.google.verificationdemo.fakeverification";
    private static final String VALID_ASSISTANT_GO_PUBLIC_SIGNATURES_B64 = "MIIDxjCCAq6gAwIBAgIUQpOEpEV+tc0MoKDoDiFB5heFCJMwDQYJKoZIhvcNAQELBQAwdDELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDU1vdW50YWluIFZpZXcxFDASBgNVBAoTC0dvb2dsZSBJbmMuMRAwDgYDVQQLEwdBbmRyb2lkMRAwDgYDVQQDEwdBbmRyb2lkMB4XDTE3MDgyOTIxNTIyMloXDTQ3MDgyOTIxNTIyMlowdDELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDU1vdW50YWluIFZpZXcxFDASBgNVBAoTC0dvb2dsZSBJbmMuMRAwDgYDVQQLEwdBbmRyb2lkMRAwDgYDVQQDEwdBbmRyb2lkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApYSqhKG2MotkT5U/TGom1gRQzLP8iL740FsyfndkTpLsOVneOsnuwZ5A/Ib7mmQNxpORhNZtVNIrOBLb0kHvlWJmqz8+oV6eBpB+JdMbYd/nYnbiMefxxk+T4essQbhkk++L8bFmX/5V+5ToZsVM6qlwlMocTjigCSbJRj4TJiiP8PFhkgoKWlYlu4RHLjsFcUxW9/TUkE3EaUiYvAOcWlyL12dOKP18ZoUkq1rRGhg9YNhD04ZXsHQ6pGG4kU1ePnthrQu1sB0Xfw79F25sk2V6+BNOP9z2tGUqSL4r6aiOIWLTjQkVR24cEsTuMNrfvFLpfuJ1YugKbwwGqCCQswIDAQABo1AwTjAMBgNVHRMEBTADAQH/MB0GA1UdDgQWBBSKakZvplUINpCyDvLfXl+3qlnPejAfBgNVHSMEGDAWgBSKakZvplUINpCyDvLfXl+3qlnPejANBgkqhkiG9w0BAQsFAAOCAQEAHPKJf87Mlk7oQ+VPeP5laUfu5ImezSCMdgQKql0AohzaMjB6T9UJSQNkOt+C75kUilNqZJrfg5l/2g5/rV17/LZb43Z5gp/nIuuxWiSJ0pjtBLIAKLigJvv0593T/gJdh785W+Wzlu1Q1w4H+HoXOCtsr/dzind3/ahlYceWmmkV/kIb/vyVJh/OZfE7U7oKqN7E4paORUwoTn4dzG9LUdM0EkG/SEkDJZpYTHEodAeZupigXAV0iGfkS7lgZF2Jgt2Hy55Bs34XYFw+cP/AQVByqCItGfKtFwPJNzfUFQsQ8WHmYIOVRje8ChqLLd1ZyTIp+6zPmAZQgnAZyFrwwA==";
    private static final String VALID_GSA_PUBLIC_SIGNATURES_B64 = "MIIEQzCCAyugAwIBAgIJAMLgh0ZkSjCNMA0GCSqGSIb3DQEBBAUAMHQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRQwEgYDVQQKEwtHb29nbGUgSW5jLjEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDAeFw0wODA4MjEyMzEzMzRaFw0zNjAxMDcyMzEzMzRaMHQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRQwEgYDVQQKEwtHb29nbGUgSW5jLjEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDCCASAwDQYJKoZIhvcNAQEBBQADggENADCCAQgCggEBAKtWLgDYO6IIrgqWbxJOKdoR8qtW0I9Y4sypEwPpt1TTcvZApxsdyxMJZ2JORland2qSGT2y5b+3JKkedxiLDmpHpDsz2WCbdxgxRczfey5YZnTJ4VZbH0xqWVW/8lGmPav5xVwnIiJS6HXk+BVKZF+JcWjAsb/GEuq/eFdpuzSqeYTcfi6idkyugwfYwXFU1+5fZKUaRKYCwkkFQVfcAs1fXA5V+++FGfvjJ/CxURaSxaBvGdGDhfXE28LWuT9ozCl5xw4Yq5OGazvV24mZVSoOO0yZ31j7kYvtwYK6NeADwbSxDdJEqO4k//0zOHKrUiGYXtqw/A0LFFtqoZKFjnkCAQOjgdkwgdYwHQYDVR0OBBYEFMd9jMIhF1Ylmn/Tgt9r45jk14alMIGmBgNVHSMEgZ4wgZuAFMd9jMIhF1Ylmn/Tgt9r45jk14aloXikdjB0MQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEUMBIGA1UEChMLR29vZ2xlIEluYy4xEDAOBgNVBAsTB0FuZHJvaWQxEDAOBgNVBAMTB0FuZHJvaWSCCQDC4IdGZEowjTAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBAUAA4IBAQBt0lLO74UwLDYKqs6Tm8/yzKkEu116FmH4rkaymUIE0P9KaMftGlMexFlaYjzmB2OxZyl6euNXEsQH8gjwyxCUKRJNexBiGcCEyj6z+a1fuHHvkiaai+KL8W1EyNmgjmyy8AW7P+LLlkR+ho5zEHatRbM/YAnqGcFh5iZBqpknHf1SKMXFh4dd239FJ1jWYfbMDMy3NS5CTMQ2XFI1MvcyUTdZPErjQfTbQe3aDQsQcafEQPD+nqActifKZ0Np0IS9L9kR/wbNvyz6ENwPiTrjV2KRkEjH78ZMcUQXg0L3BYHJ3lc69Vs5Ddf9uUGGMYldX3WfMBEmh/9iFBDAaTCK";

    public static boolean isPackageGoogleSigned(Context context, String pkg) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkg, 64);
            if (packageInfo.signatures != null && packageInfo.signatures.length == 1) {
                String signature = Base64.encodeToString(packageInfo.signatures[0].toByteArray(), 2);
                boolean isGsaSigned = signature.equals(VALID_GSA_PUBLIC_SIGNATURES_B64);
                boolean isAssistantGoSigned = signature.equals(VALID_ASSISTANT_GO_PUBLIC_SIGNATURES_B64);
                Log.d(TAG, String.format("Package %s GSA signed status %s AssistantGo signed status %s", pkg, Boolean.valueOf(isGsaSigned), Boolean.valueOf(isAssistantGoSigned)));
                return isGsaSigned || isAssistantGoSigned;
            }
            Log.d(TAG, "Wrong number of signatures returned");
            return false;
        } catch (PackageManager.NameNotFoundException exception) {
            String valueOf = String.valueOf(exception);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 34);
            sb.append("Unexpected NameNotFoundException: ");
            sb.append(valueOf);
            Log.d(TAG, sb.toString());
            return false;
        }
    }

    public static boolean isPackageWhitelisted(Context context, boolean checkGoogleSignature) {
        String packageName = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        if (checkGoogleSignature && "user".equals(Build.TYPE) && !isPackageGoogleSigned(context, packageName)) {
            Log.d(TAG, "Package is not Google signed!");
            return false;
        } else if (SEARCH_APP_PACKAGE.equals(packageName) || ANDROID_WEAR_PACKAGE.equals(packageName) || ANDROID_AUTO_PACKAGE.equals(packageName) || ASSISTANT_GO_PACKAGE.equals(packageName)) {
            return true;
        } else {
            String valueOf = String.valueOf(packageName);
            Log.d(TAG, valueOf.length() != 0 ? "Access is not allowed for package: ".concat(valueOf) : new String("Access is not allowed for package: "));
            return false;
        }
    }

    public static void logIntentWithExtras(Intent intent) {
        Log.d(TAG, "Intent:");
        String valueOf = String.valueOf(intent);
        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 1);
        sb.append("\t");
        sb.append(valueOf);
        Log.d(TAG, sb.toString());
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.d(TAG, "Extras:");
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                Log.d(TAG, String.format("\t%s: %s", key, bundle.get(key)));
            }
        }
    }

    public static NotificationCompat.Builder setAudioContentUri(NotificationCompat.Builder builder, Uri audioContentUri) {
        if (audioContentUri == null) {
            builder.getExtras().remove(AUDIO_CONTENT_URI_KEY);
        } else {
            builder.getExtras().putString(AUDIO_CONTENT_URI_KEY, audioContentUri.toString());
        }
        return builder;
    }

    public static Uri getAudioContentUri(Notification notification) {
        return getAudioContentUriFromBundle(NotificationCompat.getExtras(notification));
    }

    public static RemoteInput.Builder setRequiresAudioInput(RemoteInput.Builder builder, boolean requiresAudioInput) {
        builder.getExtras().putBoolean(INPUT_REQUIRES_AUDIO_INPUT_KEY, requiresAudioInput);
        return builder;
    }

    public static boolean getRequiresAudioInput(RemoteInput remoteInput) {
        return getRequiresAudioInputFromBundle(remoteInput.getExtras());
    }

    public static NotificationCompat.Builder setContentId(NotificationCompat.Builder builder, Integer id) {
        if (id == null) {
            builder.getExtras().remove(CONTENT_ID_KEY);
        } else {
            builder.getExtras().putInt(CONTENT_ID_KEY, id.intValue());
        }
        return builder;
    }

    public static Integer getContentId(Notification notification) {
        return getContentIdFromBundle(NotificationCompat.getExtras(notification));
    }

    private static Integer getContentIdFromBundle(Bundle bundle) {
        if (bundle.containsKey(CONTENT_ID_KEY)) {
            return Integer.valueOf(bundle.getInt(CONTENT_ID_KEY));
        }
        return null;
    }

    private static Uri getAudioContentUriFromBundle(Bundle bundle) {
        String uriStr = bundle.getString(AUDIO_CONTENT_URI_KEY);
        if (uriStr == null) {
            return null;
        }
        return Uri.parse(uriStr);
    }

    private static boolean getRequiresAudioInputFromBundle(Bundle bundle) {
        return bundle.getBoolean(INPUT_REQUIRES_AUDIO_INPUT_KEY);
    }
}
