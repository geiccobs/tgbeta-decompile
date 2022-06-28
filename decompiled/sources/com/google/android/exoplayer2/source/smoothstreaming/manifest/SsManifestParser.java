package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.googlecode.mp4parser.boxes.AC3SpecificBox;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
/* loaded from: classes3.dex */
public class SsManifestParser implements ParsingLoadable.Parser<SsManifest> {
    private final XmlPullParserFactory xmlParserFactory;

    public SsManifestParser() {
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.ParsingLoadable.Parser
    public SsManifest parse(Uri uri, InputStream inputStream) throws IOException {
        try {
            XmlPullParser xmlParser = this.xmlParserFactory.newPullParser();
            xmlParser.setInput(inputStream, null);
            SmoothStreamingMediaParser smoothStreamingMediaParser = new SmoothStreamingMediaParser(null, uri.toString());
            return (SsManifest) smoothStreamingMediaParser.parse(xmlParser);
        } catch (XmlPullParserException e) {
            throw new ParserException(e);
        }
    }

    /* loaded from: classes3.dex */
    public static class MissingFieldException extends ParserException {
        public MissingFieldException(String fieldName) {
            super("Missing required field: " + fieldName);
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class ElementParser {
        private final String baseUri;
        private final List<Pair<String, Object>> normalizedAttributes = new LinkedList();
        private final ElementParser parent;
        private final String tag;

        protected abstract Object build();

        public ElementParser(ElementParser parent, String baseUri, String tag) {
            this.parent = parent;
            this.baseUri = baseUri;
            this.tag = tag;
        }

        public final Object parse(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
            boolean foundStartTag = false;
            int skippingElementDepth = 0;
            while (true) {
                int eventType = xmlParser.getEventType();
                switch (eventType) {
                    case 1:
                        return null;
                    case 2:
                        String tagName = xmlParser.getName();
                        if (this.tag.equals(tagName)) {
                            foundStartTag = true;
                            parseStartTag(xmlParser);
                            break;
                        } else if (foundStartTag) {
                            if (skippingElementDepth > 0) {
                                skippingElementDepth++;
                                break;
                            } else if (handleChildInline(tagName)) {
                                parseStartTag(xmlParser);
                                break;
                            } else {
                                ElementParser childElementParser = newChildParser(this, tagName, this.baseUri);
                                if (childElementParser == null) {
                                    skippingElementDepth = 1;
                                    break;
                                } else {
                                    addChild(childElementParser.parse(xmlParser));
                                    break;
                                }
                            }
                        } else {
                            break;
                        }
                    case 3:
                        if (foundStartTag) {
                            if (skippingElementDepth > 0) {
                                skippingElementDepth--;
                                break;
                            } else {
                                String tagName2 = xmlParser.getName();
                                parseEndTag(xmlParser);
                                if (handleChildInline(tagName2)) {
                                    break;
                                } else {
                                    return build();
                                }
                            }
                        } else {
                            continue;
                        }
                    case 4:
                        if (foundStartTag && skippingElementDepth == 0) {
                            parseText(xmlParser);
                            break;
                        }
                        break;
                }
                xmlParser.next();
            }
        }

        private ElementParser newChildParser(ElementParser parent, String name, String baseUri) {
            if (QualityLevelParser.TAG.equals(name)) {
                return new QualityLevelParser(parent, baseUri);
            }
            if (ProtectionParser.TAG.equals(name)) {
                return new ProtectionParser(parent, baseUri);
            }
            if (StreamIndexParser.TAG.equals(name)) {
                return new StreamIndexParser(parent, baseUri);
            }
            return null;
        }

        protected final void putNormalizedAttribute(String key, Object value) {
            this.normalizedAttributes.add(Pair.create(key, value));
        }

        protected final Object getNormalizedAttribute(String key) {
            for (int i = 0; i < this.normalizedAttributes.size(); i++) {
                Pair<String, Object> pair = this.normalizedAttributes.get(i);
                if (((String) pair.first).equals(key)) {
                    return pair.second;
                }
            }
            ElementParser elementParser = this.parent;
            if (elementParser == null) {
                return null;
            }
            return elementParser.getNormalizedAttribute(key);
        }

        protected boolean handleChildInline(String tagName) {
            return false;
        }

        protected void parseStartTag(XmlPullParser xmlParser) throws ParserException {
        }

        protected void parseText(XmlPullParser xmlParser) {
        }

        protected void parseEndTag(XmlPullParser xmlParser) {
        }

        protected void addChild(Object parsedChild) {
        }

        protected final String parseRequiredString(XmlPullParser parser, String key) throws MissingFieldException {
            String value = parser.getAttributeValue(null, key);
            if (value != null) {
                return value;
            }
            throw new MissingFieldException(key);
        }

        protected final int parseInt(XmlPullParser parser, String key, int defaultValue) throws ParserException {
            String value = parser.getAttributeValue(null, key);
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ParserException(e);
                }
            }
            return defaultValue;
        }

        protected final int parseRequiredInt(XmlPullParser parser, String key) throws ParserException {
            String value = parser.getAttributeValue(null, key);
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ParserException(e);
                }
            }
            throw new MissingFieldException(key);
        }

        protected final long parseLong(XmlPullParser parser, String key, long defaultValue) throws ParserException {
            String value = parser.getAttributeValue(null, key);
            if (value != null) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    throw new ParserException(e);
                }
            }
            return defaultValue;
        }

        protected final long parseRequiredLong(XmlPullParser parser, String key) throws ParserException {
            String value = parser.getAttributeValue(null, key);
            if (value != null) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException e) {
                    throw new ParserException(e);
                }
            }
            throw new MissingFieldException(key);
        }

        protected final boolean parseBoolean(XmlPullParser parser, String key, boolean defaultValue) {
            String value = parser.getAttributeValue(null, key);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
            return defaultValue;
        }
    }

    /* loaded from: classes3.dex */
    public static class SmoothStreamingMediaParser extends ElementParser {
        private static final String KEY_DURATION = "Duration";
        private static final String KEY_DVR_WINDOW_LENGTH = "DVRWindowLength";
        private static final String KEY_IS_LIVE = "IsLive";
        private static final String KEY_LOOKAHEAD_COUNT = "LookaheadCount";
        private static final String KEY_MAJOR_VERSION = "MajorVersion";
        private static final String KEY_MINOR_VERSION = "MinorVersion";
        private static final String KEY_TIME_SCALE = "TimeScale";
        public static final String TAG = "SmoothStreamingMedia";
        private long duration;
        private long dvrWindowLength;
        private boolean isLive;
        private int majorVersion;
        private int minorVersion;
        private long timescale;
        private int lookAheadCount = -1;
        private SsManifest.ProtectionElement protectionElement = null;
        private final List<SsManifest.StreamElement> streamElements = new LinkedList();

        public SmoothStreamingMediaParser(ElementParser parent, String baseUri) {
            super(parent, baseUri, TAG);
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public void parseStartTag(XmlPullParser parser) throws ParserException {
            this.majorVersion = parseRequiredInt(parser, KEY_MAJOR_VERSION);
            this.minorVersion = parseRequiredInt(parser, KEY_MINOR_VERSION);
            this.timescale = parseLong(parser, KEY_TIME_SCALE, 10000000L);
            this.duration = parseRequiredLong(parser, KEY_DURATION);
            this.dvrWindowLength = parseLong(parser, KEY_DVR_WINDOW_LENGTH, 0L);
            this.lookAheadCount = parseInt(parser, KEY_LOOKAHEAD_COUNT, -1);
            this.isLive = parseBoolean(parser, KEY_IS_LIVE, false);
            putNormalizedAttribute(KEY_TIME_SCALE, Long.valueOf(this.timescale));
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public void addChild(Object child) {
            if (child instanceof SsManifest.StreamElement) {
                this.streamElements.add((SsManifest.StreamElement) child);
            } else if (child instanceof SsManifest.ProtectionElement) {
                Assertions.checkState(this.protectionElement == null);
                this.protectionElement = (SsManifest.ProtectionElement) child;
            }
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public Object build() {
            SsManifest.StreamElement[] streamElementArray = new SsManifest.StreamElement[this.streamElements.size()];
            this.streamElements.toArray(streamElementArray);
            if (this.protectionElement != null) {
                DrmInitData drmInitData = new DrmInitData(new DrmInitData.SchemeData(this.protectionElement.uuid, MimeTypes.VIDEO_MP4, this.protectionElement.data));
                for (SsManifest.StreamElement streamElement : streamElementArray) {
                    int type = streamElement.type;
                    if (type == 2 || type == 1) {
                        Format[] formats = streamElement.formats;
                        for (int i = 0; i < formats.length; i++) {
                            formats[i] = formats[i].copyWithDrmInitData(drmInitData);
                        }
                    }
                }
            }
            return new SsManifest(this.majorVersion, this.minorVersion, this.timescale, this.duration, this.dvrWindowLength, this.lookAheadCount, this.isLive, this.protectionElement, streamElementArray);
        }
    }

    /* loaded from: classes3.dex */
    public static class ProtectionParser extends ElementParser {
        private static final int INITIALIZATION_VECTOR_SIZE = 8;
        public static final String KEY_SYSTEM_ID = "SystemID";
        public static final String TAG = "Protection";
        public static final String TAG_PROTECTION_HEADER = "ProtectionHeader";
        private boolean inProtectionHeader;
        private byte[] initData;
        private UUID uuid;

        public ProtectionParser(ElementParser parent, String baseUri) {
            super(parent, baseUri, TAG);
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public boolean handleChildInline(String tag) {
            return TAG_PROTECTION_HEADER.equals(tag);
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public void parseStartTag(XmlPullParser parser) {
            if (TAG_PROTECTION_HEADER.equals(parser.getName())) {
                this.inProtectionHeader = true;
                String uuidString = parser.getAttributeValue(null, KEY_SYSTEM_ID);
                this.uuid = UUID.fromString(stripCurlyBraces(uuidString));
            }
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public void parseText(XmlPullParser parser) {
            if (this.inProtectionHeader) {
                this.initData = Base64.decode(parser.getText(), 0);
            }
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public void parseEndTag(XmlPullParser parser) {
            if (TAG_PROTECTION_HEADER.equals(parser.getName())) {
                this.inProtectionHeader = false;
            }
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public Object build() {
            UUID uuid = this.uuid;
            return new SsManifest.ProtectionElement(uuid, PsshAtomUtil.buildPsshAtom(uuid, this.initData), buildTrackEncryptionBoxes(this.initData));
        }

        private static TrackEncryptionBox[] buildTrackEncryptionBoxes(byte[] initData) {
            return new TrackEncryptionBox[]{new TrackEncryptionBox(true, null, 8, getProtectionElementKeyId(initData), 0, 0, null)};
        }

        private static byte[] getProtectionElementKeyId(byte[] initData) {
            StringBuilder initDataStringBuilder = new StringBuilder();
            for (int i = 0; i < initData.length; i += 2) {
                initDataStringBuilder.append((char) initData[i]);
            }
            String initDataString = initDataStringBuilder.toString();
            String keyIdString = initDataString.substring(initDataString.indexOf("<KID>") + 5, initDataString.indexOf("</KID>"));
            byte[] keyId = Base64.decode(keyIdString, 0);
            swap(keyId, 0, 3);
            swap(keyId, 1, 2);
            swap(keyId, 4, 5);
            swap(keyId, 6, 7);
            return keyId;
        }

        private static void swap(byte[] data, int firstPosition, int secondPosition) {
            byte temp = data[firstPosition];
            data[firstPosition] = data[secondPosition];
            data[secondPosition] = temp;
        }

        private static String stripCurlyBraces(String uuidString) {
            if (uuidString.charAt(0) == '{' && uuidString.charAt(uuidString.length() - 1) == '}') {
                return uuidString.substring(1, uuidString.length() - 1);
            }
            return uuidString;
        }
    }

    /* loaded from: classes3.dex */
    public static class StreamIndexParser extends ElementParser {
        private static final String KEY_DISPLAY_HEIGHT = "DisplayHeight";
        private static final String KEY_DISPLAY_WIDTH = "DisplayWidth";
        private static final String KEY_FRAGMENT_DURATION = "d";
        private static final String KEY_FRAGMENT_REPEAT_COUNT = "r";
        private static final String KEY_FRAGMENT_START_TIME = "t";
        private static final String KEY_LANGUAGE = "Language";
        private static final String KEY_MAX_HEIGHT = "MaxHeight";
        private static final String KEY_MAX_WIDTH = "MaxWidth";
        private static final String KEY_NAME = "Name";
        private static final String KEY_SUB_TYPE = "Subtype";
        private static final String KEY_TIME_SCALE = "TimeScale";
        private static final String KEY_TYPE = "Type";
        private static final String KEY_TYPE_AUDIO = "audio";
        private static final String KEY_TYPE_TEXT = "text";
        private static final String KEY_TYPE_VIDEO = "video";
        private static final String KEY_URL = "Url";
        public static final String TAG = "StreamIndex";
        private static final String TAG_STREAM_FRAGMENT = "c";
        private final String baseUri;
        private int displayHeight;
        private int displayWidth;
        private final List<Format> formats = new LinkedList();
        private String language;
        private long lastChunkDuration;
        private int maxHeight;
        private int maxWidth;
        private String name;
        private ArrayList<Long> startTimes;
        private String subType;
        private long timescale;
        private int type;
        private String url;

        public StreamIndexParser(ElementParser parent, String baseUri) {
            super(parent, baseUri, TAG);
            this.baseUri = baseUri;
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public boolean handleChildInline(String tag) {
            return "c".equals(tag);
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public void parseStartTag(XmlPullParser parser) throws ParserException {
            if ("c".equals(parser.getName())) {
                parseStreamFragmentStartTag(parser);
            } else {
                parseStreamElementStartTag(parser);
            }
        }

        private void parseStreamFragmentStartTag(XmlPullParser parser) throws ParserException {
            int chunkIndex = this.startTimes.size();
            long startTime = parseLong(parser, "t", C.TIME_UNSET);
            if (startTime == C.TIME_UNSET) {
                if (chunkIndex == 0) {
                    startTime = 0;
                } else if (this.lastChunkDuration != -1) {
                    startTime = this.startTimes.get(chunkIndex - 1).longValue() + this.lastChunkDuration;
                } else {
                    throw new ParserException("Unable to infer start time");
                }
            }
            int chunkIndex2 = chunkIndex + 1;
            this.startTimes.add(Long.valueOf(startTime));
            this.lastChunkDuration = parseLong(parser, "d", C.TIME_UNSET);
            long repeatCount = parseLong(parser, KEY_FRAGMENT_REPEAT_COUNT, 1L);
            if (repeatCount > 1 && this.lastChunkDuration == C.TIME_UNSET) {
                throw new ParserException("Repeated chunk with unspecified duration");
            }
            for (int i = 1; i < repeatCount; i++) {
                chunkIndex2++;
                this.startTimes.add(Long.valueOf((this.lastChunkDuration * i) + startTime));
            }
        }

        private void parseStreamElementStartTag(XmlPullParser parser) throws ParserException {
            int parseType = parseType(parser);
            this.type = parseType;
            putNormalizedAttribute(KEY_TYPE, Integer.valueOf(parseType));
            if (this.type == 3) {
                this.subType = parseRequiredString(parser, KEY_SUB_TYPE);
            } else {
                this.subType = parser.getAttributeValue(null, KEY_SUB_TYPE);
            }
            putNormalizedAttribute(KEY_SUB_TYPE, this.subType);
            this.name = parser.getAttributeValue(null, KEY_NAME);
            this.url = parseRequiredString(parser, KEY_URL);
            this.maxWidth = parseInt(parser, KEY_MAX_WIDTH, -1);
            this.maxHeight = parseInt(parser, KEY_MAX_HEIGHT, -1);
            this.displayWidth = parseInt(parser, KEY_DISPLAY_WIDTH, -1);
            this.displayHeight = parseInt(parser, KEY_DISPLAY_HEIGHT, -1);
            String attributeValue = parser.getAttributeValue(null, KEY_LANGUAGE);
            this.language = attributeValue;
            putNormalizedAttribute(KEY_LANGUAGE, attributeValue);
            long parseInt = parseInt(parser, KEY_TIME_SCALE, -1);
            this.timescale = parseInt;
            if (parseInt == -1) {
                this.timescale = ((Long) getNormalizedAttribute(KEY_TIME_SCALE)).longValue();
            }
            this.startTimes = new ArrayList<>();
        }

        private int parseType(XmlPullParser parser) throws ParserException {
            String value = parser.getAttributeValue(null, KEY_TYPE);
            if (value != null) {
                if ("audio".equalsIgnoreCase(value)) {
                    return 1;
                }
                if ("video".equalsIgnoreCase(value)) {
                    return 2;
                }
                if ("text".equalsIgnoreCase(value)) {
                    return 3;
                }
                throw new ParserException("Invalid key value[" + value + "]");
            }
            throw new MissingFieldException(KEY_TYPE);
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public void addChild(Object child) {
            if (child instanceof Format) {
                this.formats.add((Format) child);
            }
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public Object build() {
            Format[] formatArray = new Format[this.formats.size()];
            this.formats.toArray(formatArray);
            return new SsManifest.StreamElement(this.baseUri, this.url, this.type, this.subType, this.timescale, this.name, this.maxWidth, this.maxHeight, this.displayWidth, this.displayHeight, this.language, formatArray, this.startTimes, this.lastChunkDuration);
        }
    }

    /* loaded from: classes3.dex */
    public static class QualityLevelParser extends ElementParser {
        private static final String KEY_BITRATE = "Bitrate";
        private static final String KEY_CHANNELS = "Channels";
        private static final String KEY_CODEC_PRIVATE_DATA = "CodecPrivateData";
        private static final String KEY_FOUR_CC = "FourCC";
        private static final String KEY_INDEX = "Index";
        private static final String KEY_LANGUAGE = "Language";
        private static final String KEY_MAX_HEIGHT = "MaxHeight";
        private static final String KEY_MAX_WIDTH = "MaxWidth";
        private static final String KEY_NAME = "Name";
        private static final String KEY_SAMPLING_RATE = "SamplingRate";
        private static final String KEY_SUB_TYPE = "Subtype";
        private static final String KEY_TYPE = "Type";
        public static final String TAG = "QualityLevel";
        private Format format;

        public QualityLevelParser(ElementParser parent, String baseUri) {
            super(parent, baseUri, TAG);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x00ea, code lost:
            if (r3.equals("DESC") != false) goto L27;
         */
        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void parseStartTag(org.xmlpull.v1.XmlPullParser r25) throws com.google.android.exoplayer2.ParserException {
            /*
                Method dump skipped, instructions count: 326
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.QualityLevelParser.parseStartTag(org.xmlpull.v1.XmlPullParser):void");
        }

        @Override // com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser.ElementParser
        public Object build() {
            return this.format;
        }

        private static List<byte[]> buildCodecSpecificData(String codecSpecificDataString) {
            ArrayList<byte[]> csd = new ArrayList<>();
            if (!TextUtils.isEmpty(codecSpecificDataString)) {
                byte[] codecPrivateData = Util.getBytesFromHexString(codecSpecificDataString);
                byte[][] split = CodecSpecificDataUtil.splitNalUnits(codecPrivateData);
                if (split == null) {
                    csd.add(codecPrivateData);
                } else {
                    Collections.addAll(csd, split);
                }
            }
            return csd;
        }

        private static String fourCCToMimeType(String fourCC) {
            if (fourCC.equalsIgnoreCase("H264") || fourCC.equalsIgnoreCase("X264") || fourCC.equalsIgnoreCase("AVC1") || fourCC.equalsIgnoreCase("DAVC")) {
                return "video/avc";
            }
            if (fourCC.equalsIgnoreCase("AAC") || fourCC.equalsIgnoreCase("AACL") || fourCC.equalsIgnoreCase("AACH") || fourCC.equalsIgnoreCase("AACP")) {
                return "audio/mp4a-latm";
            }
            if (fourCC.equalsIgnoreCase("TTML") || fourCC.equalsIgnoreCase("DFXP")) {
                return MimeTypes.APPLICATION_TTML;
            }
            if (fourCC.equalsIgnoreCase(AudioSampleEntry.TYPE8) || fourCC.equalsIgnoreCase(AC3SpecificBox.TYPE)) {
                return MimeTypes.AUDIO_AC3;
            }
            if (fourCC.equalsIgnoreCase(AudioSampleEntry.TYPE9) || fourCC.equalsIgnoreCase(EC3SpecificBox.TYPE)) {
                return MimeTypes.AUDIO_E_AC3;
            }
            if (fourCC.equalsIgnoreCase("dtsc")) {
                return MimeTypes.AUDIO_DTS;
            }
            if (fourCC.equalsIgnoreCase(AudioSampleEntry.TYPE12) || fourCC.equalsIgnoreCase(AudioSampleEntry.TYPE11)) {
                return MimeTypes.AUDIO_DTS_HD;
            }
            if (fourCC.equalsIgnoreCase(AudioSampleEntry.TYPE13)) {
                return MimeTypes.AUDIO_DTS_EXPRESS;
            }
            if (fourCC.equalsIgnoreCase("opus")) {
                return MimeTypes.AUDIO_OPUS;
            }
            return null;
        }
    }
}
