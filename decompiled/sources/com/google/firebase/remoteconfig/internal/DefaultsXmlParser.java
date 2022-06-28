package com.google.firebase.remoteconfig.internal;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes3.dex */
public class DefaultsXmlParser {
    private static final String XML_TAG_ENTRY = "entry";
    private static final String XML_TAG_KEY = "key";
    private static final String XML_TAG_VALUE = "value";

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static Map<String, String> getDefaultsFromXml(Context context, int resourceId) {
        Resources resources;
        Map<String, String> defaultsMap = new HashMap<>();
        try {
            resources = context.getResources();
        } catch (IOException | XmlPullParserException e) {
            Log.e(FirebaseRemoteConfig.TAG, "Encountered an error while parsing the defaults XML file.", e);
        }
        if (resources == null) {
            Log.e(FirebaseRemoteConfig.TAG, "Could not find the resources of the current context while trying to set defaults from an XML.");
            return defaultsMap;
        }
        XmlResourceParser xmlParser = resources.getXml(resourceId);
        String curTag = null;
        String key = null;
        String value = null;
        int eventType = xmlParser.getEventType();
        while (true) {
            char c = 1;
            if (eventType != 1) {
                if (eventType == 2) {
                    curTag = xmlParser.getName();
                } else if (eventType == 3) {
                    if (xmlParser.getName().equals(XML_TAG_ENTRY)) {
                        if (key == null || value == null) {
                            Log.w(FirebaseRemoteConfig.TAG, "An entry in the defaults XML has an invalid key and/or value tag.");
                        } else {
                            defaultsMap.put(key, value);
                        }
                        key = null;
                        value = null;
                    }
                    curTag = null;
                } else if (eventType == 4 && curTag != null) {
                    switch (curTag.hashCode()) {
                        case 106079:
                            if (curTag.equals(XML_TAG_KEY)) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case 111972721:
                            if (curTag.equals("value")) {
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                            key = xmlParser.getText();
                            break;
                        case 1:
                            value = xmlParser.getText();
                            break;
                        default:
                            Log.w(FirebaseRemoteConfig.TAG, "Encountered an unexpected tag while parsing the defaults XML.");
                            break;
                    }
                }
                eventType = xmlParser.next();
            } else {
                return defaultsMap;
            }
        }
    }
}
