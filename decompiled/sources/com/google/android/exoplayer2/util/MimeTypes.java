package com.google.android.exoplayer2.util;

import android.text.TextUtils;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.gms.location.LocationRequest;
import com.googlecode.mp4parser.boxes.AC3SpecificBox;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.mp4parser.iso14496.part30.WebVTTSampleEntry;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import java.util.ArrayList;
import org.telegram.ui.Components.voip.GroupCallGridCell;
/* loaded from: classes3.dex */
public final class MimeTypes {
    public static final String APPLICATION_CAMERA_MOTION = "application/x-camera-motion";
    public static final String APPLICATION_CEA608 = "application/cea-608";
    public static final String APPLICATION_CEA708 = "application/cea-708";
    public static final String APPLICATION_DVBSUBS = "application/dvbsubs";
    public static final String APPLICATION_EMSG = "application/x-emsg";
    public static final String APPLICATION_EXIF = "application/x-exif";
    public static final String APPLICATION_ICY = "application/x-icy";
    public static final String APPLICATION_ID3 = "application/id3";
    public static final String APPLICATION_M3U8 = "application/x-mpegURL";
    public static final String APPLICATION_MP4 = "application/mp4";
    public static final String APPLICATION_MP4CEA608 = "application/x-mp4-cea-608";
    public static final String APPLICATION_MP4VTT = "application/x-mp4-vtt";
    public static final String APPLICATION_MPD = "application/dash+xml";
    public static final String APPLICATION_PGS = "application/pgs";
    public static final String APPLICATION_RAWCC = "application/x-rawcc";
    public static final String APPLICATION_SCTE35 = "application/x-scte35";
    public static final String APPLICATION_SS = "application/vnd.ms-sstr+xml";
    public static final String APPLICATION_SUBRIP = "application/x-subrip";
    public static final String APPLICATION_TTML = "application/ttml+xml";
    public static final String APPLICATION_TX3G = "application/x-quicktime-tx3g";
    public static final String APPLICATION_VOBSUB = "application/vobsub";
    public static final String APPLICATION_WEBM = "application/webm";
    public static final String AUDIO_AAC = "audio/mp4a-latm";
    public static final String AUDIO_AC3 = "audio/ac3";
    public static final String AUDIO_AC4 = "audio/ac4";
    public static final String AUDIO_ALAC = "audio/alac";
    public static final String AUDIO_ALAW = "audio/g711-alaw";
    public static final String AUDIO_AMR_NB = "audio/3gpp";
    public static final String AUDIO_AMR_WB = "audio/amr-wb";
    public static final String AUDIO_DTS = "audio/vnd.dts";
    public static final String AUDIO_DTS_EXPRESS = "audio/vnd.dts.hd;profile=lbr";
    public static final String AUDIO_DTS_HD = "audio/vnd.dts.hd";
    public static final String AUDIO_E_AC3 = "audio/eac3";
    public static final String AUDIO_E_AC3_JOC = "audio/eac3-joc";
    public static final String AUDIO_FLAC = "audio/flac";
    public static final String AUDIO_MLAW = "audio/g711-mlaw";
    public static final String AUDIO_MP4 = "audio/mp4";
    public static final String AUDIO_MPEG = "audio/mpeg";
    public static final String AUDIO_MPEG_L1 = "audio/mpeg-L1";
    public static final String AUDIO_MPEG_L2 = "audio/mpeg-L2";
    public static final String AUDIO_MSGSM = "audio/gsm";
    public static final String AUDIO_OPUS = "audio/opus";
    public static final String AUDIO_RAW = "audio/raw";
    public static final String AUDIO_TRUEHD = "audio/true-hd";
    public static final String AUDIO_UNKNOWN = "audio/x-unknown";
    public static final String AUDIO_VORBIS = "audio/vorbis";
    public static final String AUDIO_WEBM = "audio/webm";
    public static final String BASE_TYPE_APPLICATION = "application";
    public static final String BASE_TYPE_AUDIO = "audio";
    public static final String BASE_TYPE_TEXT = "text";
    public static final String BASE_TYPE_VIDEO = "video";
    public static final String TEXT_SSA = "text/x-ssa";
    public static final String TEXT_VTT = "text/vtt";
    public static final String VIDEO_AV1 = "video/av01";
    public static final String VIDEO_DIVX = "video/divx";
    public static final String VIDEO_DOLBY_VISION = "video/dolby-vision";
    public static final String VIDEO_H263 = "video/3gpp";
    public static final String VIDEO_H264 = "video/avc";
    public static final String VIDEO_H265 = "video/hevc";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String VIDEO_MP4V = "video/mp4v-es";
    public static final String VIDEO_MPEG = "video/mpeg";
    public static final String VIDEO_MPEG2 = "video/mpeg2";
    public static final String VIDEO_UNKNOWN = "video/x-unknown";
    public static final String VIDEO_VC1 = "video/wvc1";
    public static final String VIDEO_VP8 = "video/x-vnd.on2.vp8";
    public static final String VIDEO_VP9 = "video/x-vnd.on2.vp9";
    public static final String VIDEO_WEBM = "video/webm";
    private static final ArrayList<CustomMimeType> customMimeTypes = new ArrayList<>();

    public static void registerCustomMimeType(String mimeType, String codecPrefix, int trackType) {
        CustomMimeType customMimeType = new CustomMimeType(mimeType, codecPrefix, trackType);
        int customMimeTypeCount = customMimeTypes.size();
        int i = 0;
        while (true) {
            if (i >= customMimeTypeCount) {
                break;
            }
            ArrayList<CustomMimeType> arrayList = customMimeTypes;
            if (!mimeType.equals(arrayList.get(i).mimeType)) {
                i++;
            } else {
                arrayList.remove(i);
                break;
            }
        }
        customMimeTypes.add(customMimeType);
    }

    public static boolean isAudio(String mimeType) {
        return "audio".equals(getTopLevelType(mimeType));
    }

    public static boolean isVideo(String mimeType) {
        return "video".equals(getTopLevelType(mimeType));
    }

    public static boolean isText(String mimeType) {
        return "text".equals(getTopLevelType(mimeType));
    }

    public static boolean isApplication(String mimeType) {
        return BASE_TYPE_APPLICATION.equals(getTopLevelType(mimeType));
    }

    public static boolean allSamplesAreSyncSamples(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        char c = 65535;
        switch (mimeType.hashCode()) {
            case -2123537834:
                if (mimeType.equals(AUDIO_E_AC3_JOC)) {
                    c = '\n';
                    break;
                }
                break;
            case -432837260:
                if (mimeType.equals(AUDIO_MPEG_L1)) {
                    c = 1;
                    break;
                }
                break;
            case -432837259:
                if (mimeType.equals(AUDIO_MPEG_L2)) {
                    c = 2;
                    break;
                }
                break;
            case 187078296:
                if (mimeType.equals(AUDIO_AC3)) {
                    c = '\b';
                    break;
                }
                break;
            case 187094639:
                if (mimeType.equals(AUDIO_RAW)) {
                    c = 3;
                    break;
                }
                break;
            case 1504578661:
                if (mimeType.equals(AUDIO_E_AC3)) {
                    c = '\t';
                    break;
                }
                break;
            case 1504619009:
                if (mimeType.equals(AUDIO_FLAC)) {
                    c = 7;
                    break;
                }
                break;
            case 1504831518:
                if (mimeType.equals(AUDIO_MPEG)) {
                    c = 0;
                    break;
                }
                break;
            case 1504891608:
                if (mimeType.equals(AUDIO_OPUS)) {
                    c = 6;
                    break;
                }
                break;
            case 1903231877:
                if (mimeType.equals(AUDIO_ALAW)) {
                    c = 4;
                    break;
                }
                break;
            case 1903589369:
                if (mimeType.equals(AUDIO_MLAW)) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
            case '\t':
            case '\n':
                return true;
            default:
                return false;
        }
    }

    public static String getVideoMediaMimeType(String codecs) {
        if (codecs == null) {
            return null;
        }
        String[] codecList = Util.splitCodecs(codecs);
        for (String codec : codecList) {
            String mimeType = getMediaMimeType(codec);
            if (mimeType != null && isVideo(mimeType)) {
                return mimeType;
            }
        }
        return null;
    }

    public static String getAudioMediaMimeType(String codecs) {
        if (codecs == null) {
            return null;
        }
        String[] codecList = Util.splitCodecs(codecs);
        for (String codec : codecList) {
            String mimeType = getMediaMimeType(codec);
            if (mimeType != null && isAudio(mimeType)) {
                return mimeType;
            }
        }
        return null;
    }

    public static String getMediaMimeType(String codec) {
        if (codec == null) {
            return null;
        }
        String codec2 = Util.toLowerInvariant(codec.trim());
        if (codec2.startsWith(VisualSampleEntry.TYPE3) || codec2.startsWith(VisualSampleEntry.TYPE4)) {
            return "video/avc";
        }
        if (codec2.startsWith(VisualSampleEntry.TYPE7) || codec2.startsWith(VisualSampleEntry.TYPE6)) {
            return VIDEO_H265;
        }
        if (codec2.startsWith("dvav") || codec2.startsWith("dva1") || codec2.startsWith("dvhe") || codec2.startsWith("dvh1")) {
            return VIDEO_DOLBY_VISION;
        }
        if (codec2.startsWith("av01")) {
            return VIDEO_AV1;
        }
        if (codec2.startsWith("vp9") || codec2.startsWith("vp09")) {
            return VIDEO_VP9;
        }
        if (codec2.startsWith("vp8") || codec2.startsWith("vp08")) {
            return VIDEO_VP8;
        }
        if (codec2.startsWith(AudioSampleEntry.TYPE3)) {
            String mimeType = null;
            if (codec2.startsWith("mp4a.")) {
                String objectTypeString = codec2.substring(5);
                if (objectTypeString.length() >= 2) {
                    try {
                        String objectTypeHexString = Util.toUpperInvariant(objectTypeString.substring(0, 2));
                        int objectTypeInt = Integer.parseInt(objectTypeHexString, 16);
                        mimeType = getMimeTypeFromMp4ObjectType(objectTypeInt);
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return mimeType == null ? "audio/mp4a-latm" : mimeType;
        } else if (codec2.startsWith(AudioSampleEntry.TYPE8) || codec2.startsWith(AC3SpecificBox.TYPE)) {
            return AUDIO_AC3;
        } else {
            if (codec2.startsWith(AudioSampleEntry.TYPE9) || codec2.startsWith(EC3SpecificBox.TYPE)) {
                return AUDIO_E_AC3;
            }
            if (codec2.startsWith("ec+3")) {
                return AUDIO_E_AC3_JOC;
            }
            if (codec2.startsWith("ac-4") || codec2.startsWith("dac4")) {
                return AUDIO_AC4;
            }
            if (codec2.startsWith("dtsc") || codec2.startsWith(AudioSampleEntry.TYPE13)) {
                return AUDIO_DTS;
            }
            if (codec2.startsWith(AudioSampleEntry.TYPE12) || codec2.startsWith(AudioSampleEntry.TYPE11)) {
                return AUDIO_DTS_HD;
            }
            if (codec2.startsWith("opus")) {
                return AUDIO_OPUS;
            }
            if (codec2.startsWith("vorbis")) {
                return AUDIO_VORBIS;
            }
            if (codec2.startsWith("flac")) {
                return AUDIO_FLAC;
            }
            if (codec2.startsWith(XMLSubtitleSampleEntry.TYPE)) {
                return APPLICATION_TTML;
            }
            if (codec2.startsWith(WebVTTSampleEntry.TYPE)) {
                return TEXT_VTT;
            }
            return getCustomMimeTypeForCodec(codec2);
        }
    }

    public static String getMimeTypeFromMp4ObjectType(int objectType) {
        switch (objectType) {
            case 32:
                return VIDEO_MP4V;
            case 33:
                return "video/avc";
            case 35:
                return VIDEO_H265;
            case 64:
            case 102:
            case 103:
            case LocationRequest.PRIORITY_LOW_POWER /* 104 */:
                return "audio/mp4a-latm";
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
                return VIDEO_MPEG2;
            case LocationRequest.PRIORITY_NO_POWER /* 105 */:
            case 107:
                return AUDIO_MPEG;
            case 106:
                return VIDEO_MPEG;
            case 163:
                return VIDEO_VC1;
            case GroupCallGridCell.CELL_HEIGHT /* 165 */:
                return AUDIO_AC3;
            case 166:
                return AUDIO_E_AC3;
            case 169:
            case TsExtractor.TS_STREAM_TYPE_AC4 /* 172 */:
                return AUDIO_DTS;
            case 170:
            case 171:
                return AUDIO_DTS_HD;
            case 173:
                return AUDIO_OPUS;
            case 174:
                return AUDIO_AC4;
            case 177:
                return VIDEO_VP9;
            default:
                return null;
        }
    }

    public static int getTrackType(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return -1;
        }
        if (isAudio(mimeType)) {
            return 1;
        }
        if (isVideo(mimeType)) {
            return 2;
        }
        if (isText(mimeType) || APPLICATION_CEA608.equals(mimeType) || APPLICATION_CEA708.equals(mimeType) || APPLICATION_MP4CEA608.equals(mimeType) || APPLICATION_SUBRIP.equals(mimeType) || APPLICATION_TTML.equals(mimeType) || APPLICATION_TX3G.equals(mimeType) || APPLICATION_MP4VTT.equals(mimeType) || APPLICATION_RAWCC.equals(mimeType) || APPLICATION_VOBSUB.equals(mimeType) || APPLICATION_PGS.equals(mimeType) || APPLICATION_DVBSUBS.equals(mimeType)) {
            return 3;
        }
        if (APPLICATION_ID3.equals(mimeType) || APPLICATION_EMSG.equals(mimeType) || APPLICATION_SCTE35.equals(mimeType)) {
            return 4;
        }
        if (APPLICATION_CAMERA_MOTION.equals(mimeType)) {
            return 5;
        }
        return getTrackTypeForCustomMimeType(mimeType);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getEncoding(String mimeType) {
        char c;
        switch (mimeType.hashCode()) {
            case -2123537834:
                if (mimeType.equals(AUDIO_E_AC3_JOC)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1095064472:
                if (mimeType.equals(AUDIO_DTS)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 187078296:
                if (mimeType.equals(AUDIO_AC3)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 187078297:
                if (mimeType.equals(AUDIO_AC4)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1504578661:
                if (mimeType.equals(AUDIO_E_AC3)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1504831518:
                if (mimeType.equals(AUDIO_MPEG)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1505942594:
                if (mimeType.equals(AUDIO_DTS_HD)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1556697186:
                if (mimeType.equals(AUDIO_TRUEHD)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 9;
            case 1:
                return 5;
            case 2:
                return 6;
            case 3:
                return 18;
            case 4:
                return 17;
            case 5:
                return 7;
            case 6:
                return 8;
            case 7:
                return 14;
            default:
                return 0;
        }
    }

    public static int getTrackTypeOfCodec(String codec) {
        return getTrackType(getMediaMimeType(codec));
    }

    private static String getTopLevelType(String mimeType) {
        int indexOfSlash;
        if (mimeType == null || (indexOfSlash = mimeType.indexOf(47)) == -1) {
            return null;
        }
        return mimeType.substring(0, indexOfSlash);
    }

    private static String getCustomMimeTypeForCodec(String codec) {
        int customMimeTypeCount = customMimeTypes.size();
        for (int i = 0; i < customMimeTypeCount; i++) {
            CustomMimeType customMimeType = customMimeTypes.get(i);
            if (codec.startsWith(customMimeType.codecPrefix)) {
                return customMimeType.mimeType;
            }
        }
        return null;
    }

    private static int getTrackTypeForCustomMimeType(String mimeType) {
        int customMimeTypeCount = customMimeTypes.size();
        for (int i = 0; i < customMimeTypeCount; i++) {
            CustomMimeType customMimeType = customMimeTypes.get(i);
            if (mimeType.equals(customMimeType.mimeType)) {
                return customMimeType.trackType;
            }
        }
        return -1;
    }

    private MimeTypes() {
    }

    /* loaded from: classes3.dex */
    public static final class CustomMimeType {
        public final String codecPrefix;
        public final String mimeType;
        public final int trackType;

        public CustomMimeType(String mimeType, String codecPrefix, int trackType) {
            this.mimeType = mimeType;
            this.codecPrefix = codecPrefix;
            this.trackType = trackType;
        }
    }
}
