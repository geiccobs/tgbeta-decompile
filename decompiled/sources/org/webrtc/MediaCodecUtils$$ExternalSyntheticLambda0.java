package org.webrtc;

import android.media.MediaCodecInfo;
import java.util.Comparator;
/* loaded from: classes5.dex */
public final /* synthetic */ class MediaCodecUtils$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ MediaCodecUtils$$ExternalSyntheticLambda0 INSTANCE = new MediaCodecUtils$$ExternalSyntheticLambda0();

    private /* synthetic */ MediaCodecUtils$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareTo;
        compareTo = ((MediaCodecInfo) obj).getName().compareTo(((MediaCodecInfo) obj2).getName());
        return compareTo;
    }
}
