package com.google.android.exoplayer2.mediacodec;

import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
/* loaded from: classes3.dex */
public final /* synthetic */ class MediaCodecUtil$$ExternalSyntheticLambda2 implements MediaCodecUtil.ScoreProvider {
    public static final /* synthetic */ MediaCodecUtil$$ExternalSyntheticLambda2 INSTANCE = new MediaCodecUtil$$ExternalSyntheticLambda2();

    private /* synthetic */ MediaCodecUtil$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecUtil.ScoreProvider
    public final int getScore(Object obj) {
        return MediaCodecUtil.lambda$applyWorkarounds$2((MediaCodecInfo) obj);
    }
}
