package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzav;
import com.google.mlkit.common.sdkinternal.ModelType;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzea {
    public static zzav.zzal.zzb zza(ModelType modelType) {
        switch (zzec.zza[modelType.ordinal()]) {
            case 1:
                return zzav.zzal.zzb.BASE_TRANSLATE;
            case 2:
                return zzav.zzal.zzb.AUTOML_IMAGE_LABELING;
            default:
                return zzav.zzal.zzb.TYPE_UNKNOWN;
        }
    }
}
