package com.google.android.exoplayer2;

import com.google.android.exoplayer2.drm.DrmSession;
/* loaded from: classes3.dex */
public final class FormatHolder {
    public DrmSession<?> drmSession;
    public Format format;
    public boolean includesDrmSession;

    public void clear() {
        this.includesDrmSession = false;
        this.drmSession = null;
        this.format = null;
    }
}
