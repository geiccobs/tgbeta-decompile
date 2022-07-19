package androidx.sharetarget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import androidx.sharetarget.ShareTargetCompat;
import com.huawei.hms.push.constant.RemoteMessageConst;
import java.util.ArrayList;
import java.util.List;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
class ShareTargetXmlParser {
    private static final Object GET_INSTANCE_LOCK = new Object();
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

    private static ArrayList<ShareTargetCompat> parseShareTargets(Context context) {
        ArrayList<ShareTargetCompat> arrayList = new ArrayList<>();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(context.getPackageName());
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, ConnectionsManager.RequestFlagNeedQuickAck);
        if (queryIntentActivities == null) {
            return arrayList;
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            Bundle bundle = activityInfo.metaData;
            if (bundle != null && bundle.containsKey("android.app.shortcuts")) {
                arrayList.addAll(parseShareTargets(context, activityInfo));
            }
        }
        return arrayList;
    }

    private static ArrayList<ShareTargetCompat> parseShareTargets(Context context, ActivityInfo activityInfo) {
        ShareTargetCompat parseShareTarget;
        ArrayList<ShareTargetCompat> arrayList = new ArrayList<>();
        XmlResourceParser xmlResourceParser = getXmlResourceParser(context, activityInfo);
        while (true) {
            try {
                int next = xmlResourceParser.next();
                if (next == 1) {
                    break;
                } else if (next == 2 && xmlResourceParser.getName().equals("share-target") && (parseShareTarget = parseShareTarget(xmlResourceParser)) != null) {
                    arrayList.add(parseShareTarget);
                }
            } catch (Exception e) {
                Log.e("ShareTargetXmlParser", "Failed to parse the Xml resource: ", e);
            }
        }
        xmlResourceParser.close();
        return arrayList;
    }

    private static XmlResourceParser getXmlResourceParser(Context context, ActivityInfo info) {
        XmlResourceParser loadXmlMetaData = info.loadXmlMetaData(context.getPackageManager(), "android.app.shortcuts");
        if (loadXmlMetaData != null) {
            return loadXmlMetaData;
        }
        throw new IllegalArgumentException("Failed to open android.app.shortcuts meta-data resource of " + info.name);
    }

    private static ShareTargetCompat parseShareTarget(XmlResourceParser parser) throws Exception {
        String attributeValue = getAttributeValue(parser, "targetClass");
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        while (true) {
            int next = parser.next();
            if (next != 1) {
                if (next == 2) {
                    String name = parser.getName();
                    name.hashCode();
                    if (name.equals(RemoteMessageConst.DATA)) {
                        arrayList.add(parseTargetData(parser));
                    } else if (name.equals("category")) {
                        arrayList2.add(getAttributeValue(parser, "name"));
                    }
                } else if (next == 3 && parser.getName().equals("share-target")) {
                    break;
                }
            } else {
                break;
            }
        }
        if (arrayList.isEmpty() || attributeValue == null || arrayList2.isEmpty()) {
            return null;
        }
        return new ShareTargetCompat((ShareTargetCompat.TargetData[]) arrayList.toArray(new ShareTargetCompat.TargetData[arrayList.size()]), attributeValue, (String[]) arrayList2.toArray(new String[arrayList2.size()]));
    }

    private static ShareTargetCompat.TargetData parseTargetData(XmlResourceParser parser) {
        return new ShareTargetCompat.TargetData(getAttributeValue(parser, "scheme"), getAttributeValue(parser, "host"), getAttributeValue(parser, "port"), getAttributeValue(parser, "path"), getAttributeValue(parser, "pathPattern"), getAttributeValue(parser, "pathPrefix"), getAttributeValue(parser, "mimeType"));
    }

    private static String getAttributeValue(XmlResourceParser parser, String attribute) {
        String attributeValue = parser.getAttributeValue("http://schemas.android.com/apk/res/android", attribute);
        return attributeValue == null ? parser.getAttributeValue(null, attribute) : attributeValue;
    }
}
