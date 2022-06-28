package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
/* loaded from: classes4.dex */
public class MediaCodecVideoConvertor {
    private static final int MEDIACODEC_TIMEOUT_DEFAULT = 2500;
    private static final int MEDIACODEC_TIMEOUT_INCREASED = 22000;
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    private MediaController.VideoConvertorListener callback;
    private long endPresentationTime;
    private MediaExtractor extractor;
    private MP4Builder mediaMuxer;

    public boolean convertVideo(String videoPath, File cacheFile, int rotationValue, boolean isSecret, int originalWidth, int originalHeight, int resultWidth, int resultHeight, int framerate, int bitrate, int originalBitrate, long startTime, long endTime, long avatarStartTime, boolean needCompress, long duration, MediaController.SavedFilterState savedFilterState, String paintPath, ArrayList<VideoEditedInfo.MediaEntity> mediaEntities, boolean isPhoto, MediaController.CropState cropState, boolean isRound, MediaController.VideoConvertorListener callback) {
        this.callback = callback;
        return convertVideoInternal(videoPath, cacheFile, rotationValue, isSecret, originalWidth, originalHeight, resultWidth, resultHeight, framerate, bitrate, originalBitrate, startTime, endTime, avatarStartTime, duration, needCompress, false, savedFilterState, paintPath, mediaEntities, isPhoto, cropState, isRound);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX WARN: Code restructure failed: missing block: B:553:0x0eeb, code lost:
        r8 = r99;
        r13 = r101;
        r24 = r102;
        r26 = r1;
        r35 = r2;
        r1 = r1;
        r66 = r10;
        r37 = r14;
        r18 = r21;
        r6 = r62;
        r25 = r78;
        r7 = r100;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x025d, code lost:
        r18 = r6;
        r42 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:952:0x182c, code lost:
        r13 = r101;
        r55 = r5;
        r4 = r78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:953:0x184e, code lost:
        throw new java.lang.RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + r3);
     */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 62, insn: 0x1b50: MOVE  (r6 I:??[OBJECT, ARRAY]) = (r62 I:??[OBJECT, ARRAY]), block:B:1024:0x1b43 */
    /* JADX WARN: Removed duplicated region for block: B:1029:0x1b69 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:1041:0x1baf A[Catch: all -> 0x1be9, TryCatch #128 {all -> 0x1be9, blocks: (B:1039:0x1ba6, B:1041:0x1baf, B:1053:0x1be5, B:1057:0x1bf5, B:1059:0x1bfa, B:1061:0x1c02, B:1062:0x1c05), top: B:1246:0x1ba6 }] */
    /* JADX WARN: Removed duplicated region for block: B:1053:0x1be5 A[Catch: all -> 0x1be9, TryCatch #128 {all -> 0x1be9, blocks: (B:1039:0x1ba6, B:1041:0x1baf, B:1053:0x1be5, B:1057:0x1bf5, B:1059:0x1bfa, B:1061:0x1c02, B:1062:0x1c05), top: B:1246:0x1ba6 }] */
    /* JADX WARN: Removed duplicated region for block: B:1057:0x1bf5 A[Catch: all -> 0x1be9, TryCatch #128 {all -> 0x1be9, blocks: (B:1039:0x1ba6, B:1041:0x1baf, B:1053:0x1be5, B:1057:0x1bf5, B:1059:0x1bfa, B:1061:0x1c02, B:1062:0x1c05), top: B:1246:0x1ba6 }] */
    /* JADX WARN: Removed duplicated region for block: B:1059:0x1bfa A[Catch: all -> 0x1be9, TryCatch #128 {all -> 0x1be9, blocks: (B:1039:0x1ba6, B:1041:0x1baf, B:1053:0x1be5, B:1057:0x1bf5, B:1059:0x1bfa, B:1061:0x1c02, B:1062:0x1c05), top: B:1246:0x1ba6 }] */
    /* JADX WARN: Removed duplicated region for block: B:1061:0x1c02 A[Catch: all -> 0x1be9, TryCatch #128 {all -> 0x1be9, blocks: (B:1039:0x1ba6, B:1041:0x1baf, B:1053:0x1be5, B:1057:0x1bf5, B:1059:0x1bfa, B:1061:0x1c02, B:1062:0x1c05), top: B:1246:0x1ba6 }] */
    /* JADX WARN: Removed duplicated region for block: B:1066:0x1c0e  */
    /* JADX WARN: Removed duplicated region for block: B:1084:0x1c82  */
    /* JADX WARN: Removed duplicated region for block: B:1092:0x1ca5  */
    /* JADX WARN: Removed duplicated region for block: B:1094:0x1cdc  */
    /* JADX WARN: Removed duplicated region for block: B:1109:0x1c15 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1200:0x1c89 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1233:0x0d03 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1234:0x0c89 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1306:0x0cb0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1350:0x1571 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1352:0x1553 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:217:0x0592  */
    /* JADX WARN: Removed duplicated region for block: B:218:0x0594  */
    /* JADX WARN: Removed duplicated region for block: B:280:0x07fa A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:290:0x0836 A[Catch: all -> 0x0856, TryCatch #42 {all -> 0x0856, blocks: (B:288:0x0814, B:290:0x0836, B:292:0x083b, B:294:0x0840, B:295:0x0846), top: B:1155:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:292:0x083b A[Catch: all -> 0x0856, TryCatch #42 {all -> 0x0856, blocks: (B:288:0x0814, B:290:0x0836, B:292:0x083b, B:294:0x0840, B:295:0x0846), top: B:1155:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:294:0x0840 A[Catch: all -> 0x0856, TryCatch #42 {all -> 0x0856, blocks: (B:288:0x0814, B:290:0x0836, B:292:0x083b, B:294:0x0840, B:295:0x0846), top: B:1155:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:390:0x0a64  */
    /* JADX WARN: Removed duplicated region for block: B:401:0x0aa2  */
    /* JADX WARN: Removed duplicated region for block: B:404:0x0ab1  */
    /* JADX WARN: Removed duplicated region for block: B:408:0x0ae0  */
    /* JADX WARN: Removed duplicated region for block: B:412:0x0b05 A[Catch: all -> 0x0a83, Exception -> 0x0ad0, TRY_ENTER, TRY_LEAVE, TryCatch #64 {Exception -> 0x0ad0, blocks: (B:405:0x0ab3, B:412:0x0b05), top: B:1188:0x0ab3 }] */
    /* JADX WARN: Removed duplicated region for block: B:436:0x0bb3  */
    /* JADX WARN: Removed duplicated region for block: B:457:0x0c68  */
    /* JADX WARN: Removed duplicated region for block: B:481:0x0cfe  */
    /* JADX WARN: Removed duplicated region for block: B:482:0x0d00  */
    /* JADX WARN: Removed duplicated region for block: B:538:0x0e7a  */
    /* JADX WARN: Removed duplicated region for block: B:543:0x0ebf  */
    /* JADX WARN: Removed duplicated region for block: B:545:0x0ed5  */
    /* JADX WARN: Removed duplicated region for block: B:546:0x0ed7  */
    /* JADX WARN: Removed duplicated region for block: B:551:0x0ee6 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:556:0x0f0b A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:565:0x0f47  */
    /* JADX WARN: Removed duplicated region for block: B:600:0x1006  */
    /* JADX WARN: Removed duplicated region for block: B:606:0x102b A[Catch: all -> 0x0f16, Exception -> 0x1010, TRY_ENTER, TRY_LEAVE, TryCatch #24 {Exception -> 0x1010, blocks: (B:601:0x1007, B:606:0x102b, B:613:0x1048), top: B:1131:0x1007 }] */
    /* JADX WARN: Removed duplicated region for block: B:607:0x1039  */
    /* JADX WARN: Removed duplicated region for block: B:611:0x1042  */
    /* JADX WARN: Removed duplicated region for block: B:641:0x10f3  */
    /* JADX WARN: Removed duplicated region for block: B:666:0x11cc  */
    /* JADX WARN: Removed duplicated region for block: B:669:0x11ed A[ADDED_TO_REGION, EDGE_INSN: B:669:0x11ed->B:1351:0x11f0 ?: BREAK  ] */
    /* JADX WARN: Removed duplicated region for block: B:673:0x120f  */
    /* JADX WARN: Removed duplicated region for block: B:674:0x1217  */
    /* JADX WARN: Removed duplicated region for block: B:679:0x1227  */
    /* JADX WARN: Removed duplicated region for block: B:680:0x1241  */
    /* JADX WARN: Removed duplicated region for block: B:813:0x1541  */
    /* JADX WARN: Removed duplicated region for block: B:814:0x1543  */
    /* JADX WARN: Removed duplicated region for block: B:892:0x1713 A[Catch: Exception -> 0x1723, all -> 0x184f, TRY_ENTER, TryCatch #35 {all -> 0x184f, blocks: (B:870:0x167c, B:892:0x1713, B:894:0x171b, B:909:0x1753, B:911:0x1758, B:913:0x177c, B:915:0x1789, B:923:0x17a1, B:924:0x17a7, B:926:0x17ac, B:929:0x17b6, B:933:0x17c3, B:936:0x17ca, B:938:0x17cf, B:940:0x17df, B:943:0x17f9, B:945:0x17ff, B:947:0x1804, B:948:0x1809, B:952:0x182c, B:953:0x184e), top: B:1144:0x167c }] */
    /* JADX WARN: Removed duplicated region for block: B:901:0x173c  */
    /* JADX WARN: Removed duplicated region for block: B:902:0x173f  */
    /* JADX WARN: Removed duplicated region for block: B:905:0x1749  */
    /* JADX WARN: Removed duplicated region for block: B:918:0x1790  */
    /* JADX WARN: Removed duplicated region for block: B:919:0x1795  */
    /* JADX WARN: Removed duplicated region for block: B:926:0x17ac A[Catch: Exception -> 0x1817, all -> 0x184f, TRY_LEAVE, TryCatch #30 {Exception -> 0x1817, blocks: (B:924:0x17a7, B:926:0x17ac, B:936:0x17ca, B:938:0x17cf), top: B:1140:0x17a7 }] */
    /* JADX WARN: Removed duplicated region for block: B:942:0x17ee  */
    /* JADX WARN: Removed duplicated region for block: B:945:0x17ff A[Catch: all -> 0x184f, Exception -> 0x185d, TryCatch #35 {all -> 0x184f, blocks: (B:870:0x167c, B:892:0x1713, B:894:0x171b, B:909:0x1753, B:911:0x1758, B:913:0x177c, B:915:0x1789, B:923:0x17a1, B:924:0x17a7, B:926:0x17ac, B:929:0x17b6, B:933:0x17c3, B:936:0x17ca, B:938:0x17cf, B:940:0x17df, B:943:0x17f9, B:945:0x17ff, B:947:0x1804, B:948:0x1809, B:952:0x182c, B:953:0x184e), top: B:1144:0x167c }] */
    /* JADX WARN: Removed duplicated region for block: B:949:0x1811  */
    /* JADX WARN: Type inference failed for: r10v46 */
    /* JADX WARN: Type inference failed for: r10v50 */
    /* JADX WARN: Type inference failed for: r10v51 */
    /* JADX WARN: Type inference failed for: r10v52 */
    /* JADX WARN: Type inference failed for: r10v53 */
    /* JADX WARN: Type inference failed for: r10v63 */
    /* JADX WARN: Type inference failed for: r10v64 */
    /* JADX WARN: Type inference failed for: r10v67 */
    /* JADX WARN: Type inference failed for: r10v69 */
    /* JADX WARN: Type inference failed for: r10v70 */
    /* JADX WARN: Type inference failed for: r13v194 */
    /* JADX WARN: Type inference failed for: r25v34 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean convertVideoInternal(java.lang.String r93, java.io.File r94, int r95, boolean r96, int r97, int r98, int r99, int r100, int r101, int r102, int r103, long r104, long r106, long r108, long r110, boolean r112, boolean r113, org.telegram.messenger.MediaController.SavedFilterState r114, java.lang.String r115, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r116, boolean r117, org.telegram.messenger.MediaController.CropState r118, boolean r119) {
        /*
            Method dump skipped, instructions count: 7511
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState, boolean):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:71:0x013b, code lost:
        if (r14[r10 + 3] != 1) goto L73;
     */
    /* JADX WARN: Removed duplicated region for block: B:122:0x0224  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x023e  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0241 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00e9  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00f1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private long readAndWriteTracks(android.media.MediaExtractor r32, org.telegram.messenger.video.MP4Builder r33, android.media.MediaCodec.BufferInfo r34, long r35, long r37, long r39, java.io.File r41, boolean r42) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 614
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        MediaController.VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener != null && videoConvertorListener.checkConversionCanceled()) {
            throw new ConversionCanceledException();
        }
    }

    private static String createFragmentShader(int srcWidth, int srcHeight, int dstWidth, int dstHeight, boolean external) {
        float kernelSize = Utilities.clamp((Math.max(srcWidth, srcHeight) / Math.max(dstHeight, dstWidth)) * 0.8f, 2.0f, 1.0f);
        int kernelRadius = (int) kernelSize;
        FileLog.d("source size " + srcWidth + "x" + srcHeight + "    dest size " + dstWidth + dstHeight + "   kernelRadius " + kernelRadius);
        if (external) {
            return "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + kernelRadius + ".0;\nconst float pixelSizeX = 1.0 / " + srcWidth + ".0;\nconst float pixelSizeY = 1.0 / " + srcHeight + ".0;\nuniform samplerExternalOES sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
        }
        return "precision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + kernelRadius + ".0;\nconst float pixelSizeX = 1.0 / " + srcHeight + ".0;\nconst float pixelSizeY = 1.0 / " + srcWidth + ".0;\nuniform sampler2D sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
    }

    /* loaded from: classes4.dex */
    public class ConversionCanceledException extends RuntimeException {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ConversionCanceledException() {
            super("canceled conversion");
            MediaCodecVideoConvertor.this = this$0;
        }
    }
}
