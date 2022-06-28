package org.webrtc;

import android.media.MediaCodecInfo;
import org.webrtc.EglBase;
import org.webrtc.Predicate;
/* loaded from: classes5.dex */
public class HardwareVideoDecoderFactory extends MediaCodecVideoDecoderFactory {
    private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>() { // from class: org.webrtc.HardwareVideoDecoderFactory.1
        @Override // org.webrtc.Predicate
        public /* synthetic */ Predicate<MediaCodecInfo> and(Predicate<? super MediaCodecInfo> predicate) {
            return Predicate.CC.$default$and(this, predicate);
        }

        @Override // org.webrtc.Predicate
        public /* synthetic */ Predicate<MediaCodecInfo> negate() {
            return Predicate.CC.$default$negate(this);
        }

        @Override // org.webrtc.Predicate
        public /* synthetic */ Predicate<MediaCodecInfo> or(Predicate<? super MediaCodecInfo> predicate) {
            return Predicate.CC.$default$or(this, predicate);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:15:0x002d, code lost:
            if (r4.equals(com.google.android.exoplayer2.util.MimeTypes.VIDEO_VP9) != false) goto L26;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean test(android.media.MediaCodecInfo r9) {
            /*
                r8 = this;
                boolean r0 = org.webrtc.MediaCodecUtils.isHardwareAccelerated(r9)
                r1 = 0
                if (r0 != 0) goto L8
                return r1
            L8:
                java.lang.String[] r0 = r9.getSupportedTypes()
                if (r0 == 0) goto L80
                int r2 = r0.length
                if (r2 != 0) goto L13
                goto L80
            L13:
                org.telegram.messenger.voip.Instance$ServerConfig r2 = org.telegram.messenger.voip.Instance.getGlobalServerConfig()
                r3 = 0
            L18:
                int r4 = r0.length
                r5 = 1
                if (r3 >= r4) goto L7f
                r4 = r0[r3]
                r6 = -1
                int r7 = r4.hashCode()
                switch(r7) {
                    case -1662541442: goto L44;
                    case 1331836730: goto L3a;
                    case 1599127256: goto L30;
                    case 1599127257: goto L27;
                    default: goto L26;
                }
            L26:
                goto L4e
            L27:
                java.lang.String r7 = "video/x-vnd.on2.vp9"
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L26
                goto L4f
            L30:
                java.lang.String r5 = "video/x-vnd.on2.vp8"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L26
                r5 = 0
                goto L4f
            L3a:
                java.lang.String r5 = "video/avc"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L26
                r5 = 2
                goto L4f
            L44:
                java.lang.String r5 = "video/hevc"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L26
                r5 = 3
                goto L4f
            L4e:
                r5 = -1
            L4f:
                switch(r5) {
                    case 0: goto L6d;
                    case 1: goto L6a;
                    case 2: goto L58;
                    case 3: goto L55;
                    default: goto L52;
                }
            L52:
                int r3 = r3 + 1
                goto L18
            L55:
                boolean r1 = r2.enable_h265_decoder
                return r1
            L58:
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                if (r4 == 0) goto L67
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                org.telegram.messenger.ChatObject$Call r4 = r4.groupCall
                if (r4 == 0) goto L67
                return r1
            L67:
                boolean r1 = r2.enable_h264_decoder
                return r1
            L6a:
                boolean r1 = r2.enable_vp9_decoder
                return r1
            L6d:
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                if (r4 == 0) goto L7c
                org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                org.telegram.messenger.ChatObject$Call r4 = r4.groupCall
                if (r4 == 0) goto L7c
                return r1
            L7c:
                boolean r1 = r2.enable_vp8_decoder
                return r1
            L7f:
                return r5
            L80:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.HardwareVideoDecoderFactory.AnonymousClass1.test(android.media.MediaCodecInfo):boolean");
        }
    };

    @Override // org.webrtc.MediaCodecVideoDecoderFactory, org.webrtc.VideoDecoderFactory
    public /* bridge */ /* synthetic */ VideoDecoder createDecoder(VideoCodecInfo videoCodecInfo) {
        return super.createDecoder(videoCodecInfo);
    }

    @Override // org.webrtc.MediaCodecVideoDecoderFactory, org.webrtc.VideoDecoderFactory
    public /* bridge */ /* synthetic */ VideoCodecInfo[] getSupportedCodecs() {
        return super.getSupportedCodecs();
    }

    @Deprecated
    public HardwareVideoDecoderFactory() {
        this(null);
    }

    public HardwareVideoDecoderFactory(EglBase.Context sharedContext) {
        this(sharedContext, null);
    }

    public HardwareVideoDecoderFactory(EglBase.Context sharedContext, Predicate<MediaCodecInfo> codecAllowedPredicate) {
        super(sharedContext, codecAllowedPredicate == null ? defaultAllowedPredicate : codecAllowedPredicate.and(defaultAllowedPredicate));
    }
}
