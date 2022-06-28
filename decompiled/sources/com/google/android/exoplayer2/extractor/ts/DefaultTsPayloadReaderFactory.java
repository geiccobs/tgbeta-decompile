package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.text.cea.Cea708InitializationData;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class DefaultTsPayloadReaderFactory implements TsPayloadReader.Factory {
    private static final int DESCRIPTOR_TAG_CAPTION_SERVICE = 134;
    public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
    public static final int FLAG_DETECT_ACCESS_UNITS = 8;
    public static final int FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS = 64;
    public static final int FLAG_IGNORE_AAC_STREAM = 2;
    public static final int FLAG_IGNORE_H264_STREAM = 4;
    public static final int FLAG_IGNORE_SPLICE_INFO_STREAM = 16;
    public static final int FLAG_OVERRIDE_CAPTION_DESCRIPTORS = 32;
    private final List<Format> closedCaptionFormats;
    private final int flags;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    public DefaultTsPayloadReaderFactory() {
        this(0);
    }

    public DefaultTsPayloadReaderFactory(int flags) {
        this(flags, Collections.singletonList(Format.createTextSampleFormat(null, MimeTypes.APPLICATION_CEA608, 0, null)));
    }

    public DefaultTsPayloadReaderFactory(int flags, List<Format> closedCaptionFormats) {
        this.flags = flags;
        this.closedCaptionFormats = closedCaptionFormats;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.TsPayloadReader.Factory
    public SparseArray<TsPayloadReader> createInitialPayloadReaders() {
        return new SparseArray<>();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.android.exoplayer2.extractor.ts.TsPayloadReader.Factory
    public TsPayloadReader createPayloadReader(int streamType, TsPayloadReader.EsInfo esInfo) {
        switch (streamType) {
            case 2:
                return new PesReader(new H262Reader(buildUserDataReader(esInfo)));
            case 3:
            case 4:
                return new PesReader(new MpegAudioReader(esInfo.language));
            case 15:
                if (!isSet(2)) {
                    return new PesReader(new AdtsReader(false, esInfo.language));
                }
                return null;
            case 17:
                if (!isSet(2)) {
                    return new PesReader(new LatmReader(esInfo.language));
                }
                return null;
            case 21:
                return new PesReader(new Id3Reader());
            case 27:
                if (!isSet(4)) {
                    return new PesReader(new H264Reader(buildSeiReader(esInfo), isSet(1), isSet(8)));
                }
                return null;
            case 36:
                return new PesReader(new H265Reader(buildSeiReader(esInfo)));
            case TsExtractor.TS_STREAM_TYPE_DVBSUBS /* 89 */:
                return new PesReader(new DvbSubtitleReader(esInfo.dvbSubtitleInfos));
            case TsExtractor.TS_STREAM_TYPE_AC3 /* 129 */:
            case TsExtractor.TS_STREAM_TYPE_E_AC3 /* 135 */:
                return new PesReader(new Ac3Reader(esInfo.language));
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /* 130 */:
                if (!isSet(64)) {
                    return null;
                }
                break;
            case 134:
                if (!isSet(16)) {
                    return new SectionReader(new SpliceInfoSectionReader());
                }
                return null;
            case TsExtractor.TS_STREAM_TYPE_DTS /* 138 */:
                break;
            case TsExtractor.TS_STREAM_TYPE_AC4 /* 172 */:
                return new PesReader(new Ac4Reader(esInfo.language));
            default:
                return null;
        }
        return new PesReader(new DtsReader(esInfo.language));
    }

    private SeiReader buildSeiReader(TsPayloadReader.EsInfo esInfo) {
        return new SeiReader(getClosedCaptionFormats(esInfo));
    }

    private UserDataReader buildUserDataReader(TsPayloadReader.EsInfo esInfo) {
        return new UserDataReader(getClosedCaptionFormats(esInfo));
    }

    private List<Format> getClosedCaptionFormats(TsPayloadReader.EsInfo esInfo) {
        int accessibilityChannel;
        String mimeType;
        if (isSet(32)) {
            return this.closedCaptionFormats;
        }
        ParsableByteArray scratchDescriptorData = new ParsableByteArray(esInfo.descriptorBytes);
        List<Format> closedCaptionFormats = this.closedCaptionFormats;
        while (scratchDescriptorData.bytesLeft() > 0) {
            int descriptorTag = scratchDescriptorData.readUnsignedByte();
            int descriptorLength = scratchDescriptorData.readUnsignedByte();
            int nextDescriptorPosition = scratchDescriptorData.getPosition() + descriptorLength;
            if (descriptorTag == 134) {
                closedCaptionFormats = new ArrayList();
                int numberOfServices = scratchDescriptorData.readUnsignedByte() & 31;
                for (int i = 0; i < numberOfServices; i++) {
                    String language = scratchDescriptorData.readString(3);
                    int captionTypeByte = scratchDescriptorData.readUnsignedByte();
                    boolean isWideAspectRatio = false;
                    boolean isDigital = (captionTypeByte & 128) != 0;
                    if (isDigital) {
                        mimeType = MimeTypes.APPLICATION_CEA708;
                        accessibilityChannel = captionTypeByte & 63;
                    } else {
                        mimeType = MimeTypes.APPLICATION_CEA608;
                        accessibilityChannel = 1;
                    }
                    byte flags = (byte) scratchDescriptorData.readUnsignedByte();
                    scratchDescriptorData.skipBytes(1);
                    List<byte[]> initializationData = null;
                    if (isDigital) {
                        if ((flags & 64) != 0) {
                            isWideAspectRatio = true;
                        }
                        initializationData = Cea708InitializationData.buildData(isWideAspectRatio);
                    }
                    closedCaptionFormats.add(Format.createTextSampleFormat(null, mimeType, null, -1, 0, language, accessibilityChannel, null, Long.MAX_VALUE, initializationData));
                }
            }
            scratchDescriptorData.setPosition(nextDescriptorPosition);
        }
        return closedCaptionFormats;
    }

    private boolean isSet(int flag) {
        return (this.flags & flag) != 0;
    }
}
