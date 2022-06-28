package org.webrtc;

import com.google.android.exoplayer2.util.MimeTypes;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public enum VideoCodecMimeType {
    VP8(MimeTypes.VIDEO_VP8),
    VP9(MimeTypes.VIDEO_VP9),
    H264("video/avc"),
    H265(MimeTypes.VIDEO_H265),
    AV1(MimeTypes.VIDEO_AV1);
    
    private final String mimeType;

    VideoCodecMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String mimeType() {
        return this.mimeType;
    }
}
