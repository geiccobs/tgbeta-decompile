package com.google.android.exoplayer2.source;

import android.net.Uri;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.offline.StreamKey;
import java.util.List;
/* loaded from: classes3.dex */
public interface MediaSourceFactory {
    MediaSource createMediaSource(Uri uri);

    int[] getSupportedTypes();

    MediaSourceFactory setDrmSessionManager(DrmSessionManager<?> drmSessionManager);

    MediaSourceFactory setStreamKeys(List<StreamKey> list);

    /* renamed from: com.google.android.exoplayer2.source.MediaSourceFactory$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static MediaSourceFactory $default$setStreamKeys(MediaSourceFactory _this, List list) {
            return _this;
        }
    }
}
