package com.google.android.exoplayer2.drm;

import android.util.Pair;
import java.util.Map;
/* loaded from: classes.dex */
public final class WidevineUtil {
    public static Pair<Long, Long> getLicenseDurationRemainingSec(DrmSession<?> drmSession) {
        Map<String, String> queryKeyStatus = drmSession.queryKeyStatus();
        if (queryKeyStatus == null) {
            return null;
        }
        return new Pair<>(Long.valueOf(getDurationRemainingSec(queryKeyStatus, "LicenseDurationRemaining")), Long.valueOf(getDurationRemainingSec(queryKeyStatus, "PlaybackDurationRemaining")));
    }

    private static long getDurationRemainingSec(Map<String, String> map, String str) {
        if (map != null) {
            try {
                String str2 = map.get(str);
                if (str2 == null) {
                    return -9223372036854775807L;
                }
                return Long.parseLong(str2);
            } catch (NumberFormatException unused) {
                return -9223372036854775807L;
            }
        }
        return -9223372036854775807L;
    }
}
