package com.google.android.exoplayer2.text.cea;

import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class Cea708InitializationData {
    public static List<byte[]> buildData(boolean z) {
        return Collections.singletonList(new byte[]{z ? (byte) 1 : (byte) 0});
    }
}
