package org.telegram.ui.Components;

import android.util.Pair;
import java.util.Comparator;
/* loaded from: classes5.dex */
public final /* synthetic */ class VideoPlayerSeekBar$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ VideoPlayerSeekBar$$ExternalSyntheticLambda1 INSTANCE = new VideoPlayerSeekBar$$ExternalSyntheticLambda1();

    private /* synthetic */ VideoPlayerSeekBar$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return VideoPlayerSeekBar.lambda$updateTimestamps$1((Pair) obj, (Pair) obj2);
    }
}
