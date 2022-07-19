package com.huawei.agconnect.config.impl;

import android.util.Log;
import com.huawei.agconnect.AGCRoutePolicy;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public final class Utils {
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
                Log.e("Utils", "Exception when closing the 'Closeable'.");
            }
        }
    }

    public static void copy(Reader reader, Writer writer) throws IOException {
        copy(reader, writer, new char[4096]);
    }

    public static void copy(Reader reader, Writer writer, char[] cArr) throws IOException {
        while (true) {
            int read = reader.read(cArr);
            if (-1 != read) {
                writer.write(cArr, 0, read);
            } else {
                return;
            }
        }
    }

    public static Map<String, String> fixKeyPathMap(Map<String, String> map) {
        HashMap hashMap = new HashMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            hashMap.put(fixPath(entry.getKey()), entry.getValue());
        }
        return hashMap;
    }

    public static String fixPath(String str) {
        int i = 0;
        if (str.length() > 0) {
            while (str.charAt(i) == '/') {
                i++;
            }
        }
        return "/" + str.substring(i);
    }

    public static AGCRoutePolicy getRoutePolicyFromJson(String str, String str2) {
        if (str != null) {
            char c = 65535;
            switch (str.hashCode()) {
                case 2155:
                    if (str.equals("CN")) {
                        c = 0;
                        break;
                    }
                    break;
                case 2177:
                    if (str.equals("DE")) {
                        c = 1;
                        break;
                    }
                    break;
                case 2627:
                    if (str.equals("RU")) {
                        c = 2;
                        break;
                    }
                    break;
                case 2644:
                    if (str.equals("SG")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return AGCRoutePolicy.CHINA;
                case 1:
                    return AGCRoutePolicy.GERMANY;
                case 2:
                    return AGCRoutePolicy.RUSSIA;
                case 3:
                    return AGCRoutePolicy.SINGAPORE;
            }
        }
        if (str2 != null) {
            if (str2.contains("connect-drcn")) {
                return AGCRoutePolicy.CHINA;
            }
            if (str2.contains("connect-dre")) {
                return AGCRoutePolicy.GERMANY;
            }
            if (str2.contains("connect-drru")) {
                return AGCRoutePolicy.RUSSIA;
            }
            if (str2.contains("connect-dra")) {
                return AGCRoutePolicy.SINGAPORE;
            }
        }
        return AGCRoutePolicy.UNKNOWN;
    }

    public static String toString(InputStream inputStream, String str) throws UnsupportedEncodingException, IOException {
        StringWriter stringWriter = new StringWriter();
        copy(new InputStreamReader(inputStream, str), stringWriter);
        return stringWriter.toString();
    }
}
