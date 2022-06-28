package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.PendingResultUtil;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final class zap implements PendingResultUtil.zaa {
    @Override // com.google.android.gms.common.internal.PendingResultUtil.zaa
    public final ApiException zaa(Status status) {
        return ApiExceptionUtil.fromStatus(status);
    }
}
