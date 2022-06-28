package org.webrtc;

import com.google.android.exoplayer2.C;
import java.io.UnsupportedEncodingException;
import java.util.Map;
/* loaded from: classes5.dex */
class JniHelper {
    JniHelper() {
    }

    static byte[] getStringBytes(String s) {
        try {
            return s.getBytes(C.ISO88591_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("ISO-8859-1 is unsupported");
        }
    }

    static Object getStringClass() {
        return String.class;
    }

    static Object getKey(Map.Entry entry) {
        return entry.getKey();
    }

    static Object getValue(Map.Entry entry) {
        return entry.getValue();
    }
}
