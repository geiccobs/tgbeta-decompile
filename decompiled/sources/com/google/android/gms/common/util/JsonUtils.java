package com.google.android.gms.common.util;

import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class JsonUtils {
    private static final Pattern zza = Pattern.compile("\\\\.");
    private static final Pattern zzb = Pattern.compile("[\\\\\"/\b\f\n\r\t]");

    private JsonUtils() {
    }

    public static String unescapeString(String str) {
        if (!TextUtils.isEmpty(str)) {
            String zza2 = zzc.zza(str);
            Matcher matcher = zza.matcher(zza2);
            StringBuffer stringBuffer = null;
            while (matcher.find()) {
                if (stringBuffer == null) {
                    stringBuffer = new StringBuffer();
                }
                switch (matcher.group().charAt(1)) {
                    case '\"':
                        matcher.appendReplacement(stringBuffer, "\"");
                        break;
                    case '/':
                        matcher.appendReplacement(stringBuffer, "/");
                        break;
                    case '\\':
                        matcher.appendReplacement(stringBuffer, "\\\\");
                        break;
                    case 'b':
                        matcher.appendReplacement(stringBuffer, "\b");
                        break;
                    case 'f':
                        matcher.appendReplacement(stringBuffer, "\f");
                        break;
                    case 'n':
                        matcher.appendReplacement(stringBuffer, "\n");
                        break;
                    case 'r':
                        matcher.appendReplacement(stringBuffer, "\r");
                        break;
                    case 't':
                        matcher.appendReplacement(stringBuffer, "\t");
                        break;
                    default:
                        throw new IllegalStateException("Found an escaped character that should never be.");
                }
            }
            if (stringBuffer == null) {
                return zza2;
            }
            matcher.appendTail(stringBuffer);
            return stringBuffer.toString();
        }
        return str;
    }

    public static String escapeString(String str) {
        if (!TextUtils.isEmpty(str)) {
            Matcher matcher = zzb.matcher(str);
            StringBuffer stringBuffer = null;
            while (matcher.find()) {
                if (stringBuffer == null) {
                    stringBuffer = new StringBuffer();
                }
                switch (matcher.group().charAt(0)) {
                    case '\b':
                        matcher.appendReplacement(stringBuffer, "\\\\b");
                        break;
                    case '\t':
                        matcher.appendReplacement(stringBuffer, "\\\\t");
                        break;
                    case '\n':
                        matcher.appendReplacement(stringBuffer, "\\\\n");
                        break;
                    case '\f':
                        matcher.appendReplacement(stringBuffer, "\\\\f");
                        break;
                    case '\r':
                        matcher.appendReplacement(stringBuffer, "\\\\r");
                        break;
                    case '\"':
                        matcher.appendReplacement(stringBuffer, "\\\\\\\"");
                        break;
                    case '/':
                        matcher.appendReplacement(stringBuffer, "\\\\/");
                        break;
                    case '\\':
                        matcher.appendReplacement(stringBuffer, "\\\\\\\\");
                        break;
                }
            }
            if (stringBuffer == null) {
                return str;
            }
            matcher.appendTail(stringBuffer);
            return stringBuffer.toString();
        }
        return str;
    }

    public static boolean areJsonValuesEquivalent(Object obj, Object obj2) {
        if (obj == null && obj2 == null) {
            return true;
        }
        if (obj == null || obj2 == null) {
            return false;
        }
        if ((obj instanceof JSONObject) && (obj2 instanceof JSONObject)) {
            JSONObject jSONObject = (JSONObject) obj;
            JSONObject jSONObject2 = (JSONObject) obj2;
            if (jSONObject.length() != jSONObject2.length()) {
                return false;
            }
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                if (!jSONObject2.has(next)) {
                    return false;
                }
                try {
                    if (!areJsonValuesEquivalent(jSONObject.get((String) Preconditions.checkNotNull(next)), jSONObject2.get(next))) {
                        return false;
                    }
                } catch (JSONException e) {
                    return false;
                }
            }
            return true;
        } else if ((obj instanceof JSONArray) && (obj2 instanceof JSONArray)) {
            JSONArray jSONArray = (JSONArray) obj;
            JSONArray jSONArray2 = (JSONArray) obj2;
            if (jSONArray.length() != jSONArray2.length()) {
                return false;
            }
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    if (!areJsonValuesEquivalent(jSONArray.get(i), jSONArray2.get(i))) {
                        return false;
                    }
                } catch (JSONException e2) {
                    return false;
                }
            }
            return true;
        } else {
            return obj.equals(obj2);
        }
    }
}
