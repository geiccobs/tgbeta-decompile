package androidx.sharetarget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import androidx.collection.ArrayMap;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.util.AtomicFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
class ShortcutsInfoSerialization {
    private static final String ATTR_ACTION = "action";
    private static final String ATTR_COMPONENT = "component";
    private static final String ATTR_DISABLED_MSG = "disabled_message";
    private static final String ATTR_ICON_BMP_PATH = "icon_bitmap_path";
    private static final String ATTR_ICON_RES_NAME = "icon_resource_name";
    private static final String ATTR_ID = "id";
    private static final String ATTR_LONG_LABEL = "long_label";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_RANK = "rank";
    private static final String ATTR_SHORT_LABEL = "short_label";
    private static final String ATTR_TARGET_CLASS = "targetClass";
    private static final String ATTR_TARGET_PACKAGE = "targetPackage";
    private static final String TAG = "ShortcutInfoCompatSaver";
    private static final String TAG_CATEGORY = "categories";
    private static final String TAG_INTENT = "intent";
    private static final String TAG_ROOT = "share_targets";
    private static final String TAG_TARGET = "target";

    private ShortcutsInfoSerialization() {
    }

    /* loaded from: classes3.dex */
    public static class ShortcutContainer {
        final String mBitmapPath;
        final String mResourceName;
        final ShortcutInfoCompat mShortcutInfo;

        public ShortcutContainer(ShortcutInfoCompat shortcut, String resourceName, String bitmapPath) {
            this.mShortcutInfo = shortcut;
            this.mResourceName = resourceName;
            this.mBitmapPath = bitmapPath;
        }
    }

    public static void saveAsXml(List<ShortcutContainer> shortcutsList, File output) {
        AtomicFile atomicFile = new AtomicFile(output);
        FileOutputStream fileStream = null;
        try {
            fileStream = atomicFile.startWrite();
            BufferedOutputStream stream = new BufferedOutputStream(fileStream);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(stream, "UTF_8");
            serializer.startDocument(null, true);
            serializer.startTag(null, TAG_ROOT);
            for (ShortcutContainer shortcut : shortcutsList) {
                serializeShortcutContainer(serializer, shortcut);
            }
            serializer.endTag(null, TAG_ROOT);
            serializer.endDocument();
            stream.flush();
            fileStream.flush();
            atomicFile.finishWrite(fileStream);
        } catch (Exception e) {
            Log.e(TAG, "Failed to write to file " + atomicFile.getBaseFile(), e);
            atomicFile.failWrite(fileStream);
            throw new RuntimeException("Failed to write to file " + atomicFile.getBaseFile(), e);
        }
    }

    public static Map<String, ShortcutContainer> loadFromXml(File input, Context context) {
        FileInputStream stream;
        ShortcutContainer shortcut;
        Map<String, ShortcutContainer> shortcutsList = new ArrayMap<>();
        try {
            stream = new FileInputStream(input);
        } catch (Exception e) {
            input.delete();
            Log.e(TAG, "Failed to load saved values from file " + input.getAbsolutePath() + ". Old state removed, new added", e);
        }
        if (input.exists()) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, "UTF_8");
            while (true) {
                int type = parser.next();
                if (type == 1) {
                    break;
                } else if (type == 2 && parser.getName().equals(TAG_TARGET) && (shortcut = parseShortcutContainer(parser, context)) != null && shortcut.mShortcutInfo != null) {
                    shortcutsList.put(shortcut.mShortcutInfo.getId(), shortcut);
                }
            }
            stream.close();
            return shortcutsList;
        }
        stream.close();
        return shortcutsList;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0086, code lost:
        if (r12.equals(androidx.sharetarget.ShortcutsInfoSerialization.TAG_INTENT) != false) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static androidx.sharetarget.ShortcutsInfoSerialization.ShortcutContainer parseShortcutContainer(org.xmlpull.v1.XmlPullParser r18, android.content.Context r19) throws java.lang.Exception {
        /*
            r0 = r18
            java.lang.String r1 = r18.getName()
            java.lang.String r2 = "target"
            boolean r1 = r1.equals(r2)
            r3 = 0
            if (r1 != 0) goto L10
            return r3
        L10:
            java.lang.String r1 = "id"
            java.lang.String r1 = getAttributeValue(r0, r1)
            java.lang.String r4 = "short_label"
            java.lang.String r4 = getAttributeValue(r0, r4)
            boolean r5 = android.text.TextUtils.isEmpty(r1)
            if (r5 != 0) goto Lff
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L2c
            r12 = r19
            goto L101
        L2c:
            java.lang.String r3 = "rank"
            java.lang.String r3 = getAttributeValue(r0, r3)
            int r3 = java.lang.Integer.parseInt(r3)
            java.lang.String r5 = "long_label"
            java.lang.String r5 = getAttributeValue(r0, r5)
            java.lang.String r6 = "disabled_message"
            java.lang.String r6 = getAttributeValue(r0, r6)
            android.content.ComponentName r7 = parseComponentName(r18)
            java.lang.String r8 = "icon_resource_name"
            java.lang.String r8 = getAttributeValue(r0, r8)
            java.lang.String r9 = "icon_bitmap_path"
            java.lang.String r9 = getAttributeValue(r0, r9)
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            java.util.HashSet r11 = new java.util.HashSet
            r11.<init>()
        L5c:
            int r12 = r18.next()
            r13 = r12
            r14 = 0
            r15 = 1
            if (r12 == r15) goto Lb5
            r12 = 2
            if (r13 != r12) goto La8
            java.lang.String r12 = r18.getName()
            r16 = -1
            int r17 = r12.hashCode()
            switch(r17) {
                case -1183762788: goto L80;
                case 1296516636: goto L76;
                default: goto L75;
            }
        L75:
            goto L89
        L76:
            java.lang.String r14 = "categories"
            boolean r12 = r12.equals(r14)
            if (r12 == 0) goto L75
            r14 = 1
            goto L8a
        L80:
            java.lang.String r15 = "intent"
            boolean r12 = r12.equals(r15)
            if (r12 == 0) goto L75
            goto L8a
        L89:
            r14 = -1
        L8a:
            switch(r14) {
                case 0: goto L9e;
                case 1: goto L8e;
                default: goto L8d;
            }
        L8d:
            goto La7
        L8e:
            java.lang.String r12 = "name"
            java.lang.String r12 = getAttributeValue(r0, r12)
            boolean r14 = android.text.TextUtils.isEmpty(r12)
            if (r14 != 0) goto La7
            r11.add(r12)
            goto La7
        L9e:
            android.content.Intent r12 = parseIntent(r18)
            if (r12 == 0) goto La7
            r10.add(r12)
        La7:
            goto L5c
        La8:
            r12 = 3
            if (r13 != r12) goto L5c
            java.lang.String r12 = r18.getName()
            boolean r12 = r12.equals(r2)
            if (r12 == 0) goto L5c
        Lb5:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = new androidx.core.content.pm.ShortcutInfoCompat$Builder
            r12 = r19
            r2.<init>(r12, r1)
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = r2.setShortLabel(r4)
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = r2.setRank(r3)
            boolean r15 = android.text.TextUtils.isEmpty(r5)
            if (r15 != 0) goto Lcd
            r2.setLongLabel(r5)
        Lcd:
            boolean r15 = android.text.TextUtils.isEmpty(r6)
            if (r15 != 0) goto Ld6
            r2.setDisabledMessage(r6)
        Ld6:
            if (r7 == 0) goto Ldb
            r2.setActivity(r7)
        Ldb:
            boolean r15 = r10.isEmpty()
            if (r15 != 0) goto Lec
            android.content.Intent[] r14 = new android.content.Intent[r14]
            java.lang.Object[] r14 = r10.toArray(r14)
            android.content.Intent[] r14 = (android.content.Intent[]) r14
            r2.setIntents(r14)
        Lec:
            boolean r14 = r11.isEmpty()
            if (r14 != 0) goto Lf5
            r2.setCategories(r11)
        Lf5:
            androidx.sharetarget.ShortcutsInfoSerialization$ShortcutContainer r14 = new androidx.sharetarget.ShortcutsInfoSerialization$ShortcutContainer
            androidx.core.content.pm.ShortcutInfoCompat r15 = r2.build()
            r14.<init>(r15, r8, r9)
            return r14
        Lff:
            r12 = r19
        L101:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.sharetarget.ShortcutsInfoSerialization.parseShortcutContainer(org.xmlpull.v1.XmlPullParser, android.content.Context):androidx.sharetarget.ShortcutsInfoSerialization$ShortcutContainer");
    }

    private static ComponentName parseComponentName(XmlPullParser parser) {
        String value = getAttributeValue(parser, ATTR_COMPONENT);
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return ComponentName.unflattenFromString(value);
    }

    private static Intent parseIntent(XmlPullParser parser) {
        String action = getAttributeValue(parser, ATTR_ACTION);
        String targetPackage = getAttributeValue(parser, ATTR_TARGET_PACKAGE);
        String targetClass = getAttributeValue(parser, ATTR_TARGET_CLASS);
        if (action == null) {
            return null;
        }
        Intent intent = new Intent(action);
        if (!TextUtils.isEmpty(targetPackage) && !TextUtils.isEmpty(targetClass)) {
            intent.setClassName(targetPackage, targetClass);
        }
        return intent;
    }

    private static String getAttributeValue(XmlPullParser parser, String attribute) {
        String value = parser.getAttributeValue("http://schemas.android.com/apk/res/android", attribute);
        if (value == null) {
            return parser.getAttributeValue(null, attribute);
        }
        return value;
    }

    private static void serializeShortcutContainer(XmlSerializer serializer, ShortcutContainer container) throws IOException {
        Intent[] intents;
        serializer.startTag(null, TAG_TARGET);
        ShortcutInfoCompat shortcut = container.mShortcutInfo;
        serializeAttribute(serializer, "id", shortcut.getId());
        serializeAttribute(serializer, ATTR_SHORT_LABEL, shortcut.getShortLabel().toString());
        serializeAttribute(serializer, ATTR_RANK, Integer.toString(shortcut.getRank()));
        if (!TextUtils.isEmpty(shortcut.getLongLabel())) {
            serializeAttribute(serializer, ATTR_LONG_LABEL, shortcut.getLongLabel().toString());
        }
        if (!TextUtils.isEmpty(shortcut.getDisabledMessage())) {
            serializeAttribute(serializer, ATTR_DISABLED_MSG, shortcut.getDisabledMessage().toString());
        }
        if (shortcut.getActivity() != null) {
            serializeAttribute(serializer, ATTR_COMPONENT, shortcut.getActivity().flattenToString());
        }
        if (!TextUtils.isEmpty(container.mResourceName)) {
            serializeAttribute(serializer, ATTR_ICON_RES_NAME, container.mResourceName);
        }
        if (!TextUtils.isEmpty(container.mBitmapPath)) {
            serializeAttribute(serializer, ATTR_ICON_BMP_PATH, container.mBitmapPath);
        }
        for (Intent intent : shortcut.getIntents()) {
            serializeIntent(serializer, intent);
        }
        for (String category : shortcut.getCategories()) {
            serializeCategory(serializer, category);
        }
        serializer.endTag(null, TAG_TARGET);
    }

    private static void serializeIntent(XmlSerializer serializer, Intent intent) throws IOException {
        serializer.startTag(null, TAG_INTENT);
        serializeAttribute(serializer, ATTR_ACTION, intent.getAction());
        if (intent.getComponent() != null) {
            serializeAttribute(serializer, ATTR_TARGET_PACKAGE, intent.getComponent().getPackageName());
            serializeAttribute(serializer, ATTR_TARGET_CLASS, intent.getComponent().getClassName());
        }
        serializer.endTag(null, TAG_INTENT);
    }

    private static void serializeCategory(XmlSerializer serializer, String category) throws IOException {
        if (TextUtils.isEmpty(category)) {
            return;
        }
        serializer.startTag(null, TAG_CATEGORY);
        serializeAttribute(serializer, "name", category);
        serializer.endTag(null, TAG_CATEGORY);
    }

    private static void serializeAttribute(XmlSerializer serializer, String attribute, String value) throws IOException {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        serializer.attribute(null, attribute, value);
    }
}
