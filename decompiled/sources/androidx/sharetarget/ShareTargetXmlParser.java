package androidx.sharetarget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import androidx.sharetarget.ShareTargetCompat;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
class ShareTargetXmlParser {
    private static final String ATTR_HOST = "host";
    private static final String ATTR_MIME_TYPE = "mimeType";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PATH = "path";
    private static final String ATTR_PATH_PATTERN = "pathPattern";
    private static final String ATTR_PATH_PREFIX = "pathPrefix";
    private static final String ATTR_PORT = "port";
    private static final String ATTR_SCHEME = "scheme";
    private static final String ATTR_TARGET_CLASS = "targetClass";
    private static final Object GET_INSTANCE_LOCK = new Object();
    private static final String META_DATA_APP_SHORTCUTS = "android.app.shortcuts";
    static final String TAG = "ShareTargetXmlParser";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DATA = "data";
    private static final String TAG_SHARE_TARGET = "share-target";
    private static volatile ArrayList<ShareTargetCompat> sShareTargets;

    public static ArrayList<ShareTargetCompat> getShareTargets(Context context) {
        if (sShareTargets == null) {
            synchronized (GET_INSTANCE_LOCK) {
                if (sShareTargets == null) {
                    sShareTargets = parseShareTargets(context);
                }
            }
        }
        return sShareTargets;
    }

    private ShareTargetXmlParser() {
    }

    private static ArrayList<ShareTargetCompat> parseShareTargets(Context context) {
        ArrayList<ShareTargetCompat> targets = new ArrayList<>();
        Intent mainIntent = new Intent("android.intent.action.MAIN");
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        mainIntent.setPackage(context.getPackageName());
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(mainIntent, 128);
        if (resolveInfos == null) {
            return targets;
        }
        for (ResolveInfo info : resolveInfos) {
            ActivityInfo activityInfo = info.activityInfo;
            Bundle metaData = activityInfo.metaData;
            if (metaData != null && metaData.containsKey(META_DATA_APP_SHORTCUTS)) {
                List<ShareTargetCompat> shareTargets = parseShareTargets(context, activityInfo);
                targets.addAll(shareTargets);
            }
        }
        return targets;
    }

    private static ArrayList<ShareTargetCompat> parseShareTargets(Context context, ActivityInfo activityInfo) {
        ShareTargetCompat target;
        ArrayList<ShareTargetCompat> targets = new ArrayList<>();
        XmlResourceParser parser = getXmlResourceParser(context, activityInfo);
        while (true) {
            try {
                int type = parser.next();
                if (type == 1) {
                    break;
                } else if (type == 2 && parser.getName().equals(TAG_SHARE_TARGET) && (target = parseShareTarget(parser)) != null) {
                    targets.add(target);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse the Xml resource: ", e);
            }
        }
        parser.close();
        return targets;
    }

    private static XmlResourceParser getXmlResourceParser(Context context, ActivityInfo info) {
        XmlResourceParser parser = info.loadXmlMetaData(context.getPackageManager(), META_DATA_APP_SHORTCUTS);
        if (parser == null) {
            throw new IllegalArgumentException("Failed to open android.app.shortcuts meta-data resource of " + info.name);
        }
        return parser;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:10:0x002e, code lost:
        if (r3.equals(androidx.sharetarget.ShareTargetXmlParser.TAG_CATEGORY) != false) goto L15;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static androidx.sharetarget.ShareTargetCompat parseShareTarget(android.content.res.XmlResourceParser r8) throws java.lang.Exception {
        /*
            java.lang.String r0 = "targetClass"
            java.lang.String r0 = getAttributeValue(r8, r0)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
        L10:
            int r3 = r8.next()
            r4 = r3
            r5 = 1
            if (r3 == r5) goto L62
            r3 = 2
            if (r4 != r3) goto L53
            java.lang.String r3 = r8.getName()
            r6 = -1
            int r7 = r3.hashCode()
            switch(r7) {
                case 3076010: goto L31;
                case 50511102: goto L28;
                default: goto L27;
            }
        L27:
            goto L3b
        L28:
            java.lang.String r7 = "category"
            boolean r3 = r3.equals(r7)
            if (r3 == 0) goto L27
            goto L3c
        L31:
            java.lang.String r5 = "data"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L27
            r5 = 0
            goto L3c
        L3b:
            r5 = -1
        L3c:
            switch(r5) {
                case 0: goto L4a;
                case 1: goto L40;
                default: goto L3f;
            }
        L3f:
            goto L52
        L40:
            java.lang.String r3 = "name"
            java.lang.String r3 = getAttributeValue(r8, r3)
            r2.add(r3)
            goto L52
        L4a:
            androidx.sharetarget.ShareTargetCompat$TargetData r3 = parseTargetData(r8)
            r1.add(r3)
        L52:
            goto L10
        L53:
            r3 = 3
            if (r4 != r3) goto L10
            java.lang.String r3 = r8.getName()
            java.lang.String r5 = "share-target"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L10
        L62:
            boolean r3 = r1.isEmpty()
            if (r3 != 0) goto L8f
            if (r0 == 0) goto L8f
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L71
            goto L8f
        L71:
            androidx.sharetarget.ShareTargetCompat r3 = new androidx.sharetarget.ShareTargetCompat
            int r5 = r1.size()
            androidx.sharetarget.ShareTargetCompat$TargetData[] r5 = new androidx.sharetarget.ShareTargetCompat.TargetData[r5]
            java.lang.Object[] r5 = r1.toArray(r5)
            androidx.sharetarget.ShareTargetCompat$TargetData[] r5 = (androidx.sharetarget.ShareTargetCompat.TargetData[]) r5
            int r6 = r2.size()
            java.lang.String[] r6 = new java.lang.String[r6]
            java.lang.Object[] r6 = r2.toArray(r6)
            java.lang.String[] r6 = (java.lang.String[]) r6
            r3.<init>(r5, r0, r6)
            return r3
        L8f:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.sharetarget.ShareTargetXmlParser.parseShareTarget(android.content.res.XmlResourceParser):androidx.sharetarget.ShareTargetCompat");
    }

    private static ShareTargetCompat.TargetData parseTargetData(XmlResourceParser parser) {
        String scheme = getAttributeValue(parser, ATTR_SCHEME);
        String host = getAttributeValue(parser, ATTR_HOST);
        String port = getAttributeValue(parser, ATTR_PORT);
        String path = getAttributeValue(parser, ATTR_PATH);
        String pathPattern = getAttributeValue(parser, ATTR_PATH_PATTERN);
        String pathPrefix = getAttributeValue(parser, ATTR_PATH_PREFIX);
        String mimeType = getAttributeValue(parser, ATTR_MIME_TYPE);
        return new ShareTargetCompat.TargetData(scheme, host, port, path, pathPattern, pathPrefix, mimeType);
    }

    private static String getAttributeValue(XmlResourceParser parser, String attribute) {
        String value = parser.getAttributeValue("http://schemas.android.com/apk/res/android", attribute);
        if (value == null) {
            return parser.getAttributeValue(null, attribute);
        }
        return value;
    }
}
