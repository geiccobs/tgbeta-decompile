package com.google.mlkit.common;

import android.content.Context;
import com.google.mlkit.common.sdkinternal.MlKitContext;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class MlKit {
    private MlKit() {
    }

    public static void initialize(Context context) {
        MlKitContext.zza(context);
    }
}
