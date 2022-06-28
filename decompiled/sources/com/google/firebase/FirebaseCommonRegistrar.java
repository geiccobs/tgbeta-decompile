package com.google.firebase;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.heartbeatinfo.DefaultHeartBeatInfo;
import com.google.firebase.platforminfo.DefaultUserAgentPublisher;
import com.google.firebase.platforminfo.KotlinDetector;
import com.google.firebase.platforminfo.LibraryVersionComponent;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class FirebaseCommonRegistrar implements ComponentRegistrar {
    private static final String ANDROID_INSTALLER = "android-installer";
    private static final String ANDROID_PLATFORM = "android-platform";
    private static final String DEVICE_BRAND = "device-brand";
    private static final String DEVICE_MODEL = "device-model";
    private static final String DEVICE_NAME = "device-name";
    private static final String FIREBASE_ANDROID = "fire-android";
    private static final String FIREBASE_COMMON = "fire-core";
    private static final String KOTLIN = "kotlin";
    private static final String MIN_SDK = "android-min-sdk";
    private static final String TARGET_SDK = "android-target-sdk";

    @Override // com.google.firebase.components.ComponentRegistrar
    public List<Component<?>> getComponents() {
        List<Component<?>> result = new ArrayList<>();
        result.add(DefaultUserAgentPublisher.component());
        result.add(DefaultHeartBeatInfo.component());
        result.add(LibraryVersionComponent.create(FIREBASE_ANDROID, String.valueOf(Build.VERSION.SDK_INT)));
        result.add(LibraryVersionComponent.create(FIREBASE_COMMON, BuildConfig.VERSION_NAME));
        result.add(LibraryVersionComponent.create(DEVICE_NAME, safeValue(Build.PRODUCT)));
        result.add(LibraryVersionComponent.create(DEVICE_MODEL, safeValue(Build.DEVICE)));
        result.add(LibraryVersionComponent.create(DEVICE_BRAND, safeValue(Build.BRAND)));
        result.add(LibraryVersionComponent.fromContext(TARGET_SDK, FirebaseCommonRegistrar$$ExternalSyntheticLambda0.INSTANCE));
        result.add(LibraryVersionComponent.fromContext(MIN_SDK, FirebaseCommonRegistrar$$ExternalSyntheticLambda1.INSTANCE));
        result.add(LibraryVersionComponent.fromContext(ANDROID_PLATFORM, FirebaseCommonRegistrar$$ExternalSyntheticLambda2.INSTANCE));
        result.add(LibraryVersionComponent.fromContext(ANDROID_INSTALLER, FirebaseCommonRegistrar$$ExternalSyntheticLambda3.INSTANCE));
        String kotlinVersion = KotlinDetector.detectVersion();
        if (kotlinVersion != null) {
            result.add(LibraryVersionComponent.create(KOTLIN, kotlinVersion));
        }
        return result;
    }

    public static /* synthetic */ String lambda$getComponents$0(Context ctx) {
        ApplicationInfo info = ctx.getApplicationInfo();
        if (info != null) {
            return String.valueOf(info.targetSdkVersion);
        }
        return "";
    }

    public static /* synthetic */ String lambda$getComponents$1(Context ctx) {
        ApplicationInfo info = ctx.getApplicationInfo();
        if (info != null && Build.VERSION.SDK_INT >= 24) {
            return String.valueOf(info.minSdkVersion);
        }
        return "";
    }

    public static /* synthetic */ String lambda$getComponents$2(Context ctx) {
        if (Build.VERSION.SDK_INT >= 16 && ctx.getPackageManager().hasSystemFeature("android.hardware.type.television")) {
            return "tv";
        }
        if (Build.VERSION.SDK_INT >= 20 && ctx.getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
            return "watch";
        }
        if (Build.VERSION.SDK_INT >= 23 && ctx.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            return "auto";
        }
        if (Build.VERSION.SDK_INT >= 26 && ctx.getPackageManager().hasSystemFeature("android.hardware.type.embedded")) {
            return "embedded";
        }
        return "";
    }

    private static String safeValue(String value) {
        return value.replace(' ', '_').replace('/', '_');
    }
}
