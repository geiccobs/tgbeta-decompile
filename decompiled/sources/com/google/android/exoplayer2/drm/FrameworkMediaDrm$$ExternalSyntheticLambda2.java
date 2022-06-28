package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.drm.ExoMediaDrm;
import java.util.UUID;
/* loaded from: classes3.dex */
public final /* synthetic */ class FrameworkMediaDrm$$ExternalSyntheticLambda2 implements ExoMediaDrm.Provider {
    public static final /* synthetic */ FrameworkMediaDrm$$ExternalSyntheticLambda2 INSTANCE = new FrameworkMediaDrm$$ExternalSyntheticLambda2();

    private /* synthetic */ FrameworkMediaDrm$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm.Provider
    public final ExoMediaDrm acquireExoMediaDrm(UUID uuid) {
        return FrameworkMediaDrm.lambda$static$0(uuid);
    }
}
