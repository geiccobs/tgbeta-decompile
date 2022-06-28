package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.text.cea.Cea608Decoder;
import com.google.android.exoplayer2.text.cea.Cea708Decoder;
import com.google.android.exoplayer2.text.dvb.DvbDecoder;
import com.google.android.exoplayer2.text.pgs.PgsDecoder;
import com.google.android.exoplayer2.text.ssa.SsaDecoder;
import com.google.android.exoplayer2.text.subrip.SubripDecoder;
import com.google.android.exoplayer2.text.ttml.TtmlDecoder;
import com.google.android.exoplayer2.text.tx3g.Tx3gDecoder;
import com.google.android.exoplayer2.text.webvtt.Mp4WebvttDecoder;
import com.google.android.exoplayer2.text.webvtt.WebvttDecoder;
import com.google.android.exoplayer2.util.MimeTypes;
/* loaded from: classes3.dex */
public interface SubtitleDecoderFactory {
    public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory() { // from class: com.google.android.exoplayer2.text.SubtitleDecoderFactory.1
        @Override // com.google.android.exoplayer2.text.SubtitleDecoderFactory
        public boolean supportsFormat(Format format) {
            String mimeType = format.sampleMimeType;
            return MimeTypes.TEXT_VTT.equals(mimeType) || MimeTypes.TEXT_SSA.equals(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType) || MimeTypes.APPLICATION_MP4VTT.equals(mimeType) || MimeTypes.APPLICATION_SUBRIP.equals(mimeType) || MimeTypes.APPLICATION_TX3G.equals(mimeType) || MimeTypes.APPLICATION_CEA608.equals(mimeType) || MimeTypes.APPLICATION_MP4CEA608.equals(mimeType) || MimeTypes.APPLICATION_CEA708.equals(mimeType) || MimeTypes.APPLICATION_DVBSUBS.equals(mimeType) || MimeTypes.APPLICATION_PGS.equals(mimeType);
        }

        @Override // com.google.android.exoplayer2.text.SubtitleDecoderFactory
        public SubtitleDecoder createDecoder(Format format) {
            String mimeType = format.sampleMimeType;
            if (mimeType != null) {
                char c = 65535;
                switch (mimeType.hashCode()) {
                    case -1351681404:
                        if (mimeType.equals(MimeTypes.APPLICATION_DVBSUBS)) {
                            c = '\t';
                            break;
                        }
                        break;
                    case -1248334819:
                        if (mimeType.equals(MimeTypes.APPLICATION_PGS)) {
                            c = '\n';
                            break;
                        }
                        break;
                    case -1026075066:
                        if (mimeType.equals(MimeTypes.APPLICATION_MP4VTT)) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1004728940:
                        if (mimeType.equals(MimeTypes.TEXT_VTT)) {
                            c = 0;
                            break;
                        }
                        break;
                    case 691401887:
                        if (mimeType.equals(MimeTypes.APPLICATION_TX3G)) {
                            c = 5;
                            break;
                        }
                        break;
                    case 822864842:
                        if (mimeType.equals(MimeTypes.TEXT_SSA)) {
                            c = 1;
                            break;
                        }
                        break;
                    case 930165504:
                        if (mimeType.equals(MimeTypes.APPLICATION_MP4CEA608)) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1566015601:
                        if (mimeType.equals(MimeTypes.APPLICATION_CEA608)) {
                            c = 6;
                            break;
                        }
                        break;
                    case 1566016562:
                        if (mimeType.equals(MimeTypes.APPLICATION_CEA708)) {
                            c = '\b';
                            break;
                        }
                        break;
                    case 1668750253:
                        if (mimeType.equals(MimeTypes.APPLICATION_SUBRIP)) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1693976202:
                        if (mimeType.equals(MimeTypes.APPLICATION_TTML)) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        return new WebvttDecoder();
                    case 1:
                        return new SsaDecoder(format.initializationData);
                    case 2:
                        return new Mp4WebvttDecoder();
                    case 3:
                        return new TtmlDecoder();
                    case 4:
                        return new SubripDecoder();
                    case 5:
                        return new Tx3gDecoder(format.initializationData);
                    case 6:
                    case 7:
                        return new Cea608Decoder(mimeType, format.accessibilityChannel);
                    case '\b':
                        return new Cea708Decoder(format.accessibilityChannel, format.initializationData);
                    case '\t':
                        return new DvbDecoder(format.initializationData);
                    case '\n':
                        return new PgsDecoder();
                }
            }
            throw new IllegalArgumentException("Attempted to create decoder for unsupported MIME type: " + mimeType);
        }
    };

    SubtitleDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
