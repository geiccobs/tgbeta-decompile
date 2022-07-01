package com.google.android.exoplayer2.mediacodec;

import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaCodecUtil$$ExternalSyntheticLambda1 implements MediaCodecUtil.ScoreProvider {
    public static final /* synthetic */ MediaCodecUtil$$ExternalSyntheticLambda1 INSTANCE = new MediaCodecUtil$$ExternalSyntheticLambda1();

    private /* synthetic */ MediaCodecUtil$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.android.exoplayer2.mediacodec.MediaCodecUtil.ScoreProvider
    public final int getScore(Object obj) {
        int lambda$applyWorkarounds$1;
        lambda$applyWorkarounds$1 = MediaCodecUtil.lambda$applyWorkarounds$1((MediaCodecInfo) obj);
        return lambda$applyWorkarounds$1;
    }
}
