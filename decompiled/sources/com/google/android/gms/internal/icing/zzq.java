package com.google.android.gms.internal.icing;

import com.huawei.hms.push.constant.RemoteMessageConst;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public final class zzq {
    static final String[] zza = {"text1", "text2", RemoteMessageConst.Notification.ICON, "intent_action", "intent_data", "intent_data_id", "intent_extra_data", "suggest_large_icon", "intent_activity", "thing_proto"};
    private static final Map<String, Integer> zzb = new HashMap(10);

    static {
        int i = 0;
        while (true) {
            String[] strArr = zza;
            int length = strArr.length;
            if (i < 10) {
                zzb.put(strArr[i], Integer.valueOf(i));
                i++;
            } else {
                return;
            }
        }
    }

    public static String zza(int i) {
        if (i >= 0) {
            String[] strArr = zza;
            int length = strArr.length;
            if (i < 10) {
                return strArr[i];
            }
            return null;
        }
        return null;
    }
}
