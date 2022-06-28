package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import android.util.Xml;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.mp4parser.iso14496.part30.WebVTTSampleEntry;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.JoinPoint;
import org.telegram.ui.ActionBar.Theme;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
public class DashManifestParser extends DefaultHandler implements ParsingLoadable.Parser<DashManifest> {
    private static final String TAG = "MpdParser";
    private final XmlPullParserFactory xmlParserFactory;
    private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
    private static final Pattern CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
    private static final Pattern CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");

    public DashManifestParser() {
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.ParsingLoadable.Parser
    public DashManifest parse(Uri uri, InputStream inputStream) throws IOException {
        try {
            XmlPullParser xpp = this.xmlParserFactory.newPullParser();
            xpp.setInput(inputStream, null);
            int eventType = xpp.next();
            if (eventType != 2 || !"MPD".equals(xpp.getName())) {
                throw new ParserException("inputStream does not contain a valid media presentation description");
            }
            return parseMediaPresentationDescription(xpp, uri.toString());
        } catch (XmlPullParserException e) {
            throw new ParserException(e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:61:0x01ca  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01eb  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01f3 A[LOOP:0: B:17:0x0071->B:65:0x01f3, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01a5 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected com.google.android.exoplayer2.source.dash.manifest.DashManifest parseMediaPresentationDescription(org.xmlpull.v1.XmlPullParser r43, java.lang.String r44) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 534
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.dash.manifest.DashManifestParser.parseMediaPresentationDescription(org.xmlpull.v1.XmlPullParser, java.lang.String):com.google.android.exoplayer2.source.dash.manifest.DashManifest");
    }

    protected DashManifest buildMediaPresentationDescription(long availabilityStartTime, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdateTimeMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, ProgramInformation programInformation, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        return new DashManifest(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, programInformation, utcTiming, location, periods);
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xpp) {
        String schemeIdUri = xpp.getAttributeValue(null, "schemeIdUri");
        String value = xpp.getAttributeValue(null, CommonProperties.VALUE);
        return buildUtcTimingElement(schemeIdUri, value);
    }

    protected UtcTimingElement buildUtcTimingElement(String schemeIdUri, String value) {
        return new UtcTimingElement(schemeIdUri, value);
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xpp, String baseUrl, long defaultStartMs) throws XmlPullParserException, IOException {
        String baseUrl2;
        Object obj;
        List<EventStream> eventStreams;
        List<AdaptationSet> adaptationSets;
        Object obj2 = null;
        String id = xpp.getAttributeValue(null, "id");
        long startMs = parseDuration(xpp, TtmlNode.START, defaultStartMs);
        long durationMs = parseDuration(xpp, "duration", C.TIME_UNSET);
        List<AdaptationSet> adaptationSets2 = new ArrayList<>();
        List<EventStream> eventStreams2 = new ArrayList<>();
        String baseUrl3 = baseUrl;
        SegmentBase segmentBase = null;
        Descriptor assetIdentifier = null;
        boolean seenFirstBaseUrl = false;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "BaseURL")) {
                if (seenFirstBaseUrl) {
                    baseUrl2 = baseUrl3;
                    eventStreams = eventStreams2;
                    obj = obj2;
                    adaptationSets = adaptationSets2;
                } else {
                    baseUrl2 = parseBaseUrl(xpp, baseUrl3);
                    seenFirstBaseUrl = true;
                    eventStreams = eventStreams2;
                    obj = obj2;
                    adaptationSets = adaptationSets2;
                }
            } else if (XmlPullParserUtil.isStartTag(xpp, "AdaptationSet")) {
                baseUrl2 = baseUrl3;
                adaptationSets = adaptationSets2;
                eventStreams = eventStreams2;
                adaptationSets.add(parseAdaptationSet(xpp, baseUrl3, segmentBase, durationMs));
                obj = null;
            } else {
                baseUrl2 = baseUrl3;
                adaptationSets = adaptationSets2;
                eventStreams = eventStreams2;
                if (XmlPullParserUtil.isStartTag(xpp, "EventStream")) {
                    eventStreams.add(parseEventStream(xpp));
                    obj = null;
                } else if (XmlPullParserUtil.isStartTag(xpp, "SegmentBase")) {
                    obj = null;
                    segmentBase = parseSegmentBase(xpp, null);
                } else {
                    obj = null;
                    if (XmlPullParserUtil.isStartTag(xpp, "SegmentList")) {
                        segmentBase = parseSegmentList(xpp, null, durationMs);
                    } else if (XmlPullParserUtil.isStartTag(xpp, "SegmentTemplate")) {
                        segmentBase = parseSegmentTemplate(xpp, null, Collections.emptyList(), durationMs);
                    } else if (XmlPullParserUtil.isStartTag(xpp, "AssetIdentifier")) {
                        assetIdentifier = parseDescriptor(xpp, "AssetIdentifier");
                    } else {
                        maybeSkipTag(xpp);
                    }
                }
            }
            if (!XmlPullParserUtil.isEndTag(xpp, "Period")) {
                List<AdaptationSet> adaptationSets3 = adaptationSets;
                eventStreams2 = eventStreams;
                obj2 = obj;
                baseUrl3 = baseUrl2;
                adaptationSets2 = adaptationSets3;
            } else {
                return Pair.create(buildPeriod(id, startMs, adaptationSets, eventStreams, assetIdentifier), Long.valueOf(durationMs));
            }
        }
    }

    protected Period buildPeriod(String id, long startMs, List<AdaptationSet> adaptationSets, List<EventStream> eventStreams, Descriptor assetIdentifier) {
        return new Period(id, startMs, adaptationSets, eventStreams, assetIdentifier);
    }

    /* JADX WARN: Removed duplicated region for block: B:65:0x0346 A[LOOP:0: B:3:0x007c->B:65:0x0346, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0308 A[EDGE_INSN: B:66:0x0308->B:59:0x0308 ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected com.google.android.exoplayer2.source.dash.manifest.AdaptationSet parseAdaptationSet(org.xmlpull.v1.XmlPullParser r43, java.lang.String r44, com.google.android.exoplayer2.source.dash.manifest.SegmentBase r45, long r46) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 863
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.dash.manifest.DashManifestParser.parseAdaptationSet(org.xmlpull.v1.XmlPullParser, java.lang.String, com.google.android.exoplayer2.source.dash.manifest.SegmentBase, long):com.google.android.exoplayer2.source.dash.manifest.AdaptationSet");
    }

    protected AdaptationSet buildAdaptationSet(int id, int contentType, List<Representation> representations, List<Descriptor> accessibilityDescriptors, List<Descriptor> essentialProperties, List<Descriptor> supplementalProperties) {
        return new AdaptationSet(id, contentType, representations, accessibilityDescriptors, essentialProperties, supplementalProperties);
    }

    protected int parseContentType(XmlPullParser xpp) {
        String contentType = xpp.getAttributeValue(null, "contentType");
        if (TextUtils.isEmpty(contentType)) {
            return -1;
        }
        if ("audio".equals(contentType)) {
            return 1;
        }
        if ("video".equals(contentType)) {
            return 2;
        }
        return "text".equals(contentType) ? 3 : -1;
    }

    protected int getContentType(Format format) {
        String sampleMimeType = format.sampleMimeType;
        if (TextUtils.isEmpty(sampleMimeType)) {
            return -1;
        }
        if (MimeTypes.isVideo(sampleMimeType)) {
            return 2;
        }
        if (MimeTypes.isAudio(sampleMimeType)) {
            return 1;
        }
        return mimeTypeIsRawText(sampleMimeType) ? 3 : -1;
    }

    protected Pair<String, DrmInitData.SchemeData> parseContentProtection(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeType = null;
        String licenseServerUrl = null;
        byte[] data = null;
        UUID uuid = null;
        DrmInitData.SchemeData schemeData = null;
        String schemeIdUri = xpp.getAttributeValue(null, "schemeIdUri");
        if (schemeIdUri != null) {
            String lowerInvariant = Util.toLowerInvariant(schemeIdUri);
            char c = 65535;
            switch (lowerInvariant.hashCode()) {
                case 489446379:
                    if (lowerInvariant.equals("urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95")) {
                        c = 1;
                        break;
                    }
                    break;
                case 755418770:
                    if (lowerInvariant.equals("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1812765994:
                    if (lowerInvariant.equals("urn:mpeg:dash:mp4protection:2011")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    schemeType = xpp.getAttributeValue(null, CommonProperties.VALUE);
                    String defaultKid = XmlPullParserUtil.getAttributeValueIgnorePrefix(xpp, "default_KID");
                    if (!TextUtils.isEmpty(defaultKid) && !"00000000-0000-0000-0000-000000000000".equals(defaultKid)) {
                        String[] defaultKidStrings = defaultKid.split("\\s+");
                        UUID[] defaultKids = new UUID[defaultKidStrings.length];
                        for (int i = 0; i < defaultKidStrings.length; i++) {
                            defaultKids[i] = UUID.fromString(defaultKidStrings[i]);
                        }
                        data = PsshAtomUtil.buildPsshAtom(C.COMMON_PSSH_UUID, defaultKids, null);
                        uuid = C.COMMON_PSSH_UUID;
                        break;
                    }
                    break;
                case 1:
                    uuid = C.PLAYREADY_UUID;
                    break;
                case 2:
                    uuid = C.WIDEVINE_UUID;
                    break;
            }
        }
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "ms:laurl")) {
                licenseServerUrl = xpp.getAttributeValue(null, "licenseUrl");
            } else if (data == null && XmlPullParserUtil.isStartTagIgnorePrefix(xpp, ProtectionSystemSpecificHeaderBox.TYPE) && xpp.next() == 4) {
                data = Base64.decode(xpp.getText(), 0);
                uuid = PsshAtomUtil.parseUuid(data);
                if (uuid == null) {
                    Log.w(TAG, "Skipping malformed cenc:pssh data");
                    data = null;
                }
            } else if (data == null && C.PLAYREADY_UUID.equals(uuid) && XmlPullParserUtil.isStartTag(xpp, "mspr:pro") && xpp.next() == 4) {
                data = PsshAtomUtil.buildPsshAtom(C.PLAYREADY_UUID, Base64.decode(xpp.getText(), 0));
            } else {
                maybeSkipTag(xpp);
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "ContentProtection"));
        if (uuid != null) {
            schemeData = new DrmInitData.SchemeData(uuid, licenseServerUrl, MimeTypes.VIDEO_MP4, data);
        }
        return Pair.create(schemeType, schemeData);
    }

    protected void parseAdaptationSetChild(XmlPullParser xpp) throws XmlPullParserException, IOException {
        maybeSkipTag(xpp);
    }

    protected RepresentationInfo parseRepresentation(XmlPullParser xpp, String baseUrl, String adaptationSetMimeType, String adaptationSetCodecs, int adaptationSetWidth, int adaptationSetHeight, float adaptationSetFrameRate, int adaptationSetAudioChannels, int adaptationSetAudioSamplingRate, String adaptationSetLanguage, List<Descriptor> adaptationSetRoleDescriptors, List<Descriptor> adaptationSetAccessibilityDescriptors, List<Descriptor> adaptationSetEssentialProperties, List<Descriptor> adaptationSetSupplementalProperties, SegmentBase segmentBase, long periodDurationMs) throws XmlPullParserException, IOException {
        String baseUrl2;
        ArrayList<Descriptor> supplementalProperties;
        ArrayList<Descriptor> essentialProperties;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        String id = xmlPullParser.getAttributeValue(null, "id");
        int bandwidth = parseInt(xmlPullParser, "bandwidth", -1);
        String mimeType = parseString(xmlPullParser, "mimeType", adaptationSetMimeType);
        String codecs = parseString(xmlPullParser, "codecs", adaptationSetCodecs);
        int width = parseInt(xmlPullParser, "width", adaptationSetWidth);
        int height = parseInt(xmlPullParser, "height", adaptationSetHeight);
        float frameRate = parseFrameRate(xmlPullParser, adaptationSetFrameRate);
        int audioSamplingRate = parseInt(xmlPullParser, "audioSamplingRate", adaptationSetAudioSamplingRate);
        ArrayList<DrmInitData.SchemeData> drmSchemeDatas = new ArrayList<>();
        ArrayList<Descriptor> inbandEventStreams = new ArrayList<>();
        ArrayList<Descriptor> essentialProperties2 = new ArrayList<>(adaptationSetEssentialProperties);
        ArrayList<Descriptor> supplementalProperties2 = new ArrayList<>(adaptationSetSupplementalProperties);
        int audioChannels = adaptationSetAudioChannels;
        String drmSchemeType = null;
        boolean seenFirstBaseUrl = false;
        String drmSchemeType2 = baseUrl;
        SegmentBase segmentBase2 = segmentBase;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (seenFirstBaseUrl) {
                    baseUrl2 = drmSchemeType2;
                    supplementalProperties = supplementalProperties2;
                    essentialProperties = essentialProperties2;
                } else {
                    String baseUrl3 = dashManifestParser.parseBaseUrl(xmlPullParser, drmSchemeType2);
                    baseUrl2 = baseUrl3;
                    seenFirstBaseUrl = true;
                    supplementalProperties = supplementalProperties2;
                    essentialProperties = essentialProperties2;
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                audioChannels = parseAudioChannelConfiguration(xpp);
                baseUrl2 = drmSchemeType2;
                supplementalProperties = supplementalProperties2;
                essentialProperties = essentialProperties2;
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                segmentBase2 = dashManifestParser.parseSegmentBase(xmlPullParser, (SegmentBase.SingleSegmentBase) segmentBase2);
                baseUrl2 = drmSchemeType2;
                supplementalProperties = supplementalProperties2;
                essentialProperties = essentialProperties2;
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                segmentBase2 = dashManifestParser.parseSegmentList(xmlPullParser, (SegmentBase.SegmentList) segmentBase2, periodDurationMs);
                baseUrl2 = drmSchemeType2;
                supplementalProperties = supplementalProperties2;
                essentialProperties = essentialProperties2;
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                baseUrl2 = drmSchemeType2;
                supplementalProperties = supplementalProperties2;
                essentialProperties = essentialProperties2;
                segmentBase2 = parseSegmentTemplate(xpp, (SegmentBase.SegmentTemplate) segmentBase2, adaptationSetSupplementalProperties, periodDurationMs);
            } else {
                baseUrl2 = drmSchemeType2;
                supplementalProperties = supplementalProperties2;
                essentialProperties = essentialProperties2;
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                    Pair<String, DrmInitData.SchemeData> contentProtection = parseContentProtection(xpp);
                    if (contentProtection.first != null) {
                        drmSchemeType = (String) contentProtection.first;
                    }
                    if (contentProtection.second != null) {
                        drmSchemeDatas.add((DrmInitData.SchemeData) contentProtection.second);
                    }
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "InbandEventStream")) {
                    inbandEventStreams.add(parseDescriptor(xmlPullParser, "InbandEventStream"));
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "EssentialProperty")) {
                    essentialProperties.add(parseDescriptor(xmlPullParser, "EssentialProperty"));
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SupplementalProperty")) {
                    supplementalProperties.add(parseDescriptor(xmlPullParser, "SupplementalProperty"));
                } else {
                    maybeSkipTag(xpp);
                }
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                break;
            }
            xmlPullParser = xpp;
            supplementalProperties2 = supplementalProperties;
            drmSchemeType2 = baseUrl2;
            essentialProperties2 = essentialProperties;
            dashManifestParser = this;
        }
        Format format = buildFormat(id, mimeType, width, height, frameRate, audioChannels, audioSamplingRate, bandwidth, adaptationSetLanguage, adaptationSetRoleDescriptors, adaptationSetAccessibilityDescriptors, codecs, essentialProperties, supplementalProperties);
        return new RepresentationInfo(format, baseUrl2, segmentBase2 != null ? segmentBase2 : new SegmentBase.SingleSegmentBase(), drmSchemeType, drmSchemeDatas, inbandEventStreams, -1L);
    }

    protected Format buildFormat(String id, String containerMimeType, int width, int height, float frameRate, int audioChannels, int audioSamplingRate, int bitrate, String language, List<Descriptor> roleDescriptors, List<Descriptor> accessibilityDescriptors, String codecs, List<Descriptor> essentialProperties, List<Descriptor> supplementalProperties) {
        String sampleMimeType;
        String sampleMimeType2;
        int accessibilityChannel;
        String sampleMimeType3 = getSampleMimeType(containerMimeType, codecs);
        int selectionFlags = parseSelectionFlagsFromRoleDescriptors(roleDescriptors);
        int roleFlags = parseRoleFlagsFromRoleDescriptors(roleDescriptors) | parseRoleFlagsFromAccessibilityDescriptors(accessibilityDescriptors) | parseRoleFlagsFromProperties(essentialProperties) | parseRoleFlagsFromProperties(supplementalProperties);
        if (sampleMimeType3 == null) {
            sampleMimeType = sampleMimeType3;
        } else {
            if (!MimeTypes.AUDIO_E_AC3.equals(sampleMimeType3)) {
                sampleMimeType2 = sampleMimeType3;
            } else {
                sampleMimeType2 = parseEac3SupplementalProperties(supplementalProperties);
            }
            if (MimeTypes.isVideo(sampleMimeType2)) {
                return Format.createVideoContainerFormat(id, null, containerMimeType, sampleMimeType2, codecs, null, bitrate, width, height, frameRate, null, selectionFlags, roleFlags);
            }
            String sampleMimeType4 = sampleMimeType2;
            if (MimeTypes.isAudio(sampleMimeType4)) {
                return Format.createAudioContainerFormat(id, null, containerMimeType, sampleMimeType4, codecs, null, bitrate, audioChannels, audioSamplingRate, null, selectionFlags, roleFlags, language);
            }
            if (!mimeTypeIsRawText(sampleMimeType4)) {
                sampleMimeType = sampleMimeType4;
            } else {
                if (MimeTypes.APPLICATION_CEA608.equals(sampleMimeType4)) {
                    accessibilityChannel = parseCea608AccessibilityChannel(accessibilityDescriptors);
                } else if (MimeTypes.APPLICATION_CEA708.equals(sampleMimeType4)) {
                    accessibilityChannel = parseCea708AccessibilityChannel(accessibilityDescriptors);
                } else {
                    accessibilityChannel = -1;
                }
                return Format.createTextContainerFormat(id, null, containerMimeType, sampleMimeType4, codecs, bitrate, selectionFlags, roleFlags, language, accessibilityChannel);
            }
        }
        return Format.createContainerFormat(id, null, containerMimeType, sampleMimeType, codecs, bitrate, selectionFlags, roleFlags, language);
    }

    protected Representation buildRepresentation(RepresentationInfo representationInfo, String label, String extraDrmSchemeType, ArrayList<DrmInitData.SchemeData> extraDrmSchemeDatas, ArrayList<Descriptor> extraInbandEventStreams) {
        Format format = representationInfo.format;
        if (label != null) {
            format = format.copyWithLabel(label);
        }
        String drmSchemeType = representationInfo.drmSchemeType != null ? representationInfo.drmSchemeType : extraDrmSchemeType;
        ArrayList<DrmInitData.SchemeData> drmSchemeDatas = representationInfo.drmSchemeDatas;
        drmSchemeDatas.addAll(extraDrmSchemeDatas);
        if (!drmSchemeDatas.isEmpty()) {
            filterRedundantIncompleteSchemeDatas(drmSchemeDatas);
            DrmInitData drmInitData = new DrmInitData(drmSchemeType, drmSchemeDatas);
            format = format.copyWithDrmInitData(drmInitData);
        }
        ArrayList<Descriptor> inbandEventStreams = representationInfo.inbandEventStreams;
        inbandEventStreams.addAll(extraInbandEventStreams);
        return Representation.newInstance(representationInfo.revisionId, format, representationInfo.baseUrl, representationInfo.segmentBase, inbandEventStreams);
    }

    protected SegmentBase.SingleSegmentBase parseSegmentBase(XmlPullParser xpp, SegmentBase.SingleSegmentBase parent) throws XmlPullParserException, IOException {
        long indexStart;
        long indexLength;
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1L);
        long indexLength2 = 0;
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0L);
        long indexStart2 = parent != null ? parent.indexStart : 0L;
        if (parent != null) {
            indexLength2 = parent.indexLength;
        }
        RangedUri rangedUri = null;
        String indexRangeText = xpp.getAttributeValue(null, "indexRange");
        if (indexRangeText == null) {
            indexLength = indexLength2;
            indexStart = indexStart2;
        } else {
            String[] indexRange = indexRangeText.split("-");
            long indexStart3 = Long.parseLong(indexRange[0]);
            long indexLength3 = (Long.parseLong(indexRange[1]) - indexStart3) + 1;
            indexLength = indexLength3;
            indexStart = indexStart3;
        }
        if (parent != null) {
            rangedUri = parent.initialization;
        }
        RangedUri initialization = rangedUri;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "Initialization")) {
                initialization = parseInitialization(xpp);
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xpp, "SegmentBase")) {
                return buildSingleSegmentBase(initialization, timescale, presentationTimeOffset, indexStart, indexLength);
            }
        }
    }

    protected SegmentBase.SingleSegmentBase buildSingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long indexStart, long indexLength) {
        return new SegmentBase.SingleSegmentBase(initialization, timescale, presentationTimeOffset, indexStart, indexLength);
    }

    protected SegmentBase.SegmentList parseSegmentList(XmlPullParser xpp, SegmentBase.SegmentList parent, long periodDurationMs) throws XmlPullParserException, IOException {
        List<RangedUri> segments;
        List<SegmentBase.SegmentTimelineElement> timeline;
        RangedUri initialization;
        long j = 1;
        long timescale = parseLong(xpp, "timescale", parent != null ? parent.timescale : 1L);
        long presentationTimeOffset = parseLong(xpp, "presentationTimeOffset", parent != null ? parent.presentationTimeOffset : 0L);
        long duration = parseLong(xpp, "duration", parent != null ? parent.duration : C.TIME_UNSET);
        if (parent != null) {
            j = parent.startNumber;
        }
        long startNumber = parseLong(xpp, "startNumber", j);
        RangedUri initialization2 = null;
        List<SegmentBase.SegmentTimelineElement> timeline2 = null;
        List<RangedUri> segments2 = null;
        do {
            xpp.next();
            if (!XmlPullParserUtil.isStartTag(xpp, "Initialization")) {
                if (!XmlPullParserUtil.isStartTag(xpp, "SegmentTimeline")) {
                    if (XmlPullParserUtil.isStartTag(xpp, "SegmentURL")) {
                        if (segments2 == null) {
                            segments2 = new ArrayList<>();
                        }
                        segments2.add(parseSegmentUrl(xpp));
                    } else {
                        maybeSkipTag(xpp);
                    }
                } else {
                    timeline2 = parseSegmentTimeline(xpp, timescale, periodDurationMs);
                }
            } else {
                initialization2 = parseInitialization(xpp);
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "SegmentList"));
        if (parent == null) {
            initialization = initialization2;
            timeline = timeline2;
            segments = segments2;
        } else {
            RangedUri initialization3 = initialization2 != null ? initialization2 : parent.initialization;
            initialization = initialization3;
            timeline = timeline2 != null ? timeline2 : parent.segmentTimeline;
            segments = segments2 != null ? segments2 : parent.mediaSegments;
        }
        return buildSegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentBase.SegmentList buildSegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentBase.SegmentTimelineElement> timeline, List<RangedUri> segments) {
        return new SegmentBase.SegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentBase.SegmentTemplate parseSegmentTemplate(XmlPullParser xpp, SegmentBase.SegmentTemplate parent, List<Descriptor> adaptationSetSupplementalProperties, long periodDurationMs) throws XmlPullParserException, IOException {
        List<SegmentBase.SegmentTimelineElement> timeline;
        RangedUri initialization;
        XmlPullParser xmlPullParser = xpp;
        SegmentBase.SegmentTemplate segmentTemplate = parent;
        long j = 1;
        long timescale = parseLong(xmlPullParser, "timescale", segmentTemplate != null ? segmentTemplate.timescale : 1L);
        long presentationTimeOffset = parseLong(xmlPullParser, "presentationTimeOffset", segmentTemplate != null ? segmentTemplate.presentationTimeOffset : 0L);
        long duration = parseLong(xmlPullParser, "duration", segmentTemplate != null ? segmentTemplate.duration : C.TIME_UNSET);
        if (segmentTemplate != null) {
            j = segmentTemplate.startNumber;
        }
        long startNumber = parseLong(xmlPullParser, "startNumber", j);
        long endNumber = parseLastSegmentNumberSupplementalProperty(adaptationSetSupplementalProperties);
        UrlTemplate urlTemplate = null;
        UrlTemplate mediaTemplate = parseUrlTemplate(xmlPullParser, "media", segmentTemplate != null ? segmentTemplate.mediaTemplate : null);
        if (segmentTemplate != null) {
            urlTemplate = segmentTemplate.initializationTemplate;
        }
        UrlTemplate initializationTemplate = parseUrlTemplate(xmlPullParser, JoinPoint.INITIALIZATION, urlTemplate);
        RangedUri initialization2 = null;
        List<SegmentBase.SegmentTimelineElement> timeline2 = null;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                initialization2 = parseInitialization(xpp);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                timeline2 = parseSegmentTimeline(xpp, timescale, periodDurationMs);
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentTemplate")) {
                break;
            }
            xmlPullParser = xpp;
            segmentTemplate = parent;
        }
        if (segmentTemplate == null) {
            initialization = initialization2;
            timeline = timeline2;
        } else {
            initialization = initialization2 != null ? initialization2 : segmentTemplate.initialization;
            timeline = timeline2 != null ? timeline2 : segmentTemplate.segmentTimeline;
        }
        return buildSegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, endNumber, duration, timeline, initializationTemplate, mediaTemplate);
    }

    protected SegmentBase.SegmentTemplate buildSegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long endNumber, long duration, List<SegmentBase.SegmentTimelineElement> timeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate) {
        return new SegmentBase.SegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, endNumber, duration, timeline, initializationTemplate, mediaTemplate);
    }

    protected EventStream parseEventStream(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", "");
        String value = parseString(xpp, CommonProperties.VALUE, "");
        long timescale = parseLong(xpp, "timescale", 1L);
        List<Pair<Long, EventMessage>> eventMessages = new ArrayList<>();
        ByteArrayOutputStream scratchOutputStream = new ByteArrayOutputStream(512);
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "Event")) {
                eventMessages.add(parseEvent(xpp, schemeIdUri, value, timescale, scratchOutputStream));
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xpp, "EventStream")) {
                break;
            }
        }
        long[] presentationTimesUs = new long[eventMessages.size()];
        EventMessage[] events = new EventMessage[eventMessages.size()];
        for (int i = 0; i < eventMessages.size(); i++) {
            Pair<Long, EventMessage> event = eventMessages.get(i);
            presentationTimesUs[i] = ((Long) event.first).longValue();
            events[i] = (EventMessage) event.second;
        }
        return buildEventStream(schemeIdUri, value, timescale, presentationTimesUs, events);
    }

    protected EventStream buildEventStream(String schemeIdUri, String value, long timescale, long[] presentationTimesUs, EventMessage[] events) {
        return new EventStream(schemeIdUri, value, timescale, presentationTimesUs, events);
    }

    protected Pair<Long, EventMessage> parseEvent(XmlPullParser xpp, String schemeIdUri, String value, long timescale, ByteArrayOutputStream scratchOutputStream) throws IOException, XmlPullParserException {
        long id = parseLong(xpp, "id", 0L);
        long duration = parseLong(xpp, "duration", C.TIME_UNSET);
        long presentationTime = parseLong(xpp, "presentationTime", 0L);
        long durationMs = Util.scaleLargeTimestamp(duration, 1000L, timescale);
        long presentationTimesUs = Util.scaleLargeTimestamp(presentationTime, 1000000L, timescale);
        String messageData = parseString(xpp, "messageData", null);
        byte[] eventObject = parseEventObject(xpp, scratchOutputStream);
        return Pair.create(Long.valueOf(presentationTimesUs), buildEvent(schemeIdUri, value, id, durationMs, messageData == null ? eventObject : Util.getUtf8Bytes(messageData)));
    }

    protected byte[] parseEventObject(XmlPullParser xpp, ByteArrayOutputStream scratchOutputStream) throws XmlPullParserException, IOException {
        scratchOutputStream.reset();
        XmlSerializer xmlSerializer = Xml.newSerializer();
        xmlSerializer.setOutput(scratchOutputStream, "UTF-8");
        xpp.nextToken();
        while (!XmlPullParserUtil.isEndTag(xpp, "Event")) {
            switch (xpp.getEventType()) {
                case 0:
                    xmlSerializer.startDocument(null, false);
                    break;
                case 1:
                    xmlSerializer.endDocument();
                    break;
                case 2:
                    xmlSerializer.startTag(xpp.getNamespace(), xpp.getName());
                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        xmlSerializer.attribute(xpp.getAttributeNamespace(i), xpp.getAttributeName(i), xpp.getAttributeValue(i));
                    }
                    break;
                case 3:
                    xmlSerializer.endTag(xpp.getNamespace(), xpp.getName());
                    break;
                case 4:
                    xmlSerializer.text(xpp.getText());
                    break;
                case 5:
                    xmlSerializer.cdsect(xpp.getText());
                    break;
                case 6:
                    xmlSerializer.entityRef(xpp.getText());
                    break;
                case 7:
                    xmlSerializer.ignorableWhitespace(xpp.getText());
                    break;
                case 8:
                    xmlSerializer.processingInstruction(xpp.getText());
                    break;
                case 9:
                    xmlSerializer.comment(xpp.getText());
                    break;
                case 10:
                    xmlSerializer.docdecl(xpp.getText());
                    break;
            }
            xpp.nextToken();
        }
        xmlSerializer.flush();
        return scratchOutputStream.toByteArray();
    }

    protected EventMessage buildEvent(String schemeIdUri, String value, long id, long durationMs, byte[] messageData) {
        return new EventMessage(schemeIdUri, value, durationMs, id, messageData);
    }

    protected List<SegmentBase.SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xpp, long timescale, long periodDurationMs) throws XmlPullParserException, IOException {
        long startTime;
        long startTime2;
        List<SegmentBase.SegmentTimelineElement> segmentTimeline = new ArrayList<>();
        long startTime3 = 0;
        long elementDuration = -9223372036854775807L;
        int elementRepeatCount = 0;
        boolean havePreviousTimelineElement = false;
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, ExifInterface.LATITUDE_SOUTH)) {
                long newStartTime = parseLong(xpp, Theme.THEME_BACKGROUND_SLUG, C.TIME_UNSET);
                if (!havePreviousTimelineElement) {
                    long startTime4 = startTime3;
                    startTime = -9223372036854775807L;
                    startTime2 = startTime4;
                } else {
                    startTime = -9223372036854775807L;
                    startTime2 = addSegmentTimelineElementsToList(segmentTimeline, startTime3, elementDuration, elementRepeatCount, newStartTime);
                }
                if (newStartTime != startTime) {
                    startTime2 = newStartTime;
                }
                long elementDuration2 = parseLong(xpp, Theme.DEFAULT_BACKGROUND_SLUG, startTime);
                int elementRepeatCount2 = parseInt(xpp, "r", 0);
                startTime3 = startTime2;
                elementDuration = elementDuration2;
                elementRepeatCount = elementRepeatCount2;
                havePreviousTimelineElement = true;
            } else {
                maybeSkipTag(xpp);
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "SegmentTimeline"));
        if (havePreviousTimelineElement) {
            long periodDuration = Util.scaleLargeTimestamp(periodDurationMs, timescale, 1000L);
            addSegmentTimelineElementsToList(segmentTimeline, startTime3, elementDuration, elementRepeatCount, periodDuration);
        }
        return segmentTimeline;
    }

    private long addSegmentTimelineElementsToList(List<SegmentBase.SegmentTimelineElement> segmentTimeline, long startTime, long elementDuration, int elementRepeatCount, long endTime) {
        int count;
        if (elementRepeatCount >= 0) {
            count = elementRepeatCount + 1;
        } else {
            count = (int) Util.ceilDivide(endTime - startTime, elementDuration);
        }
        for (int i = 0; i < count; i++) {
            segmentTimeline.add(buildSegmentTimelineElement(startTime, elementDuration));
            startTime += elementDuration;
        }
        return startTime;
    }

    protected SegmentBase.SegmentTimelineElement buildSegmentTimelineElement(long startTime, long duration) {
        return new SegmentBase.SegmentTimelineElement(startTime, duration);
    }

    protected UrlTemplate parseUrlTemplate(XmlPullParser xpp, String name, UrlTemplate defaultValue) {
        String valueString = xpp.getAttributeValue(null, name);
        if (valueString != null) {
            return UrlTemplate.compile(valueString);
        }
        return defaultValue;
    }

    protected RangedUri parseInitialization(XmlPullParser xpp) {
        return parseRangedUrl(xpp, "sourceURL", "range");
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xpp) {
        return parseRangedUrl(xpp, "media", "mediaRange");
    }

    protected RangedUri parseRangedUrl(XmlPullParser xpp, String urlAttribute, String rangeAttribute) {
        long rangeLength;
        long rangeStart;
        String urlText = xpp.getAttributeValue(null, urlAttribute);
        String rangeText = xpp.getAttributeValue(null, rangeAttribute);
        if (rangeText == null) {
            rangeStart = 0;
            rangeLength = -1;
        } else {
            String[] rangeTextArray = rangeText.split("-");
            long rangeStart2 = Long.parseLong(rangeTextArray[0]);
            if (rangeTextArray.length != 2) {
                rangeStart = rangeStart2;
                rangeLength = -1;
            } else {
                long rangeLength2 = (Long.parseLong(rangeTextArray[1]) - rangeStart2) + 1;
                rangeStart = rangeStart2;
                rangeLength = rangeLength2;
            }
        }
        return buildRangedUri(urlText, rangeStart, rangeLength);
    }

    protected RangedUri buildRangedUri(String urlText, long rangeStart, long rangeLength) {
        return new RangedUri(urlText, rangeStart, rangeLength);
    }

    protected ProgramInformation parseProgramInformation(XmlPullParser xpp) throws IOException, XmlPullParserException {
        String title = null;
        String source = null;
        String copyright = null;
        String moreInformationURL = parseString(xpp, "moreInformationURL", null);
        String lang = parseString(xpp, "lang", null);
        do {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "Title")) {
                title = xpp.nextText();
            } else if (XmlPullParserUtil.isStartTag(xpp, "Source")) {
                source = xpp.nextText();
            } else if (XmlPullParserUtil.isStartTag(xpp, ExifInterface.TAG_COPYRIGHT)) {
                copyright = xpp.nextText();
            } else {
                maybeSkipTag(xpp);
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, "ProgramInformation"));
        return new ProgramInformation(title, source, copyright, moreInformationURL, lang);
    }

    protected String parseLabel(XmlPullParser xpp) throws XmlPullParserException, IOException {
        return parseText(xpp, "Label");
    }

    protected String parseBaseUrl(XmlPullParser xpp, String parentBaseUrl) throws XmlPullParserException, IOException {
        return UriUtil.resolve(parentBaseUrl, parseText(xpp, "BaseURL"));
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", null);
        int i = -1;
        if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(schemeIdUri)) {
            i = parseInt(xpp, CommonProperties.VALUE, -1);
        } else if ("tag:dolby.com,2014:dash:audio_channel_configuration:2011".equals(schemeIdUri) || "urn:dolby:dash:audio_channel_configuration:2011".equals(schemeIdUri)) {
            i = parseDolbyChannelConfiguration(xpp);
        }
        int audioChannels = i;
        do {
            xpp.next();
        } while (!XmlPullParserUtil.isEndTag(xpp, "AudioChannelConfiguration"));
        return audioChannels;
    }

    protected int parseSelectionFlagsFromRoleDescriptors(List<Descriptor> roleDescriptors) {
        for (int i = 0; i < roleDescriptors.size(); i++) {
            Descriptor descriptor = roleDescriptors.get(i);
            if ("urn:mpeg:dash:role:2011".equalsIgnoreCase(descriptor.schemeIdUri) && "main".equals(descriptor.value)) {
                return 1;
            }
        }
        return 0;
    }

    protected int parseRoleFlagsFromRoleDescriptors(List<Descriptor> roleDescriptors) {
        int result = 0;
        for (int i = 0; i < roleDescriptors.size(); i++) {
            Descriptor descriptor = roleDescriptors.get(i);
            if ("urn:mpeg:dash:role:2011".equalsIgnoreCase(descriptor.schemeIdUri)) {
                result |= parseDashRoleSchemeValue(descriptor.value);
            }
        }
        return result;
    }

    protected int parseRoleFlagsFromAccessibilityDescriptors(List<Descriptor> accessibilityDescriptors) {
        int result = 0;
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = accessibilityDescriptors.get(i);
            if ("urn:mpeg:dash:role:2011".equalsIgnoreCase(descriptor.schemeIdUri)) {
                result |= parseDashRoleSchemeValue(descriptor.value);
            } else if ("urn:tva:metadata:cs:AudioPurposeCS:2007".equalsIgnoreCase(descriptor.schemeIdUri)) {
                result |= parseTvaAudioPurposeCsValue(descriptor.value);
            }
        }
        return result;
    }

    protected int parseRoleFlagsFromProperties(List<Descriptor> accessibilityDescriptors) {
        int result = 0;
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = accessibilityDescriptors.get(i);
            if ("http://dashif.org/guidelines/trickmode".equalsIgnoreCase(descriptor.schemeIdUri)) {
                result |= 16384;
            }
        }
        return result;
    }

    protected int parseDashRoleSchemeValue(String value) {
        if (value == null) {
            return 0;
        }
        char c = 65535;
        switch (value.hashCode()) {
            case -2060497896:
                if (value.equals("subtitle")) {
                    c = 7;
                    break;
                }
                break;
            case -1724546052:
                if (value.equals("description")) {
                    c = '\t';
                    break;
                }
                break;
            case -1580883024:
                if (value.equals("enhanced-audio-intelligibility")) {
                    c = '\n';
                    break;
                }
                break;
            case -1408024454:
                if (value.equals("alternate")) {
                    c = 1;
                    break;
                }
                break;
            case 99825:
                if (value.equals("dub")) {
                    c = 4;
                    break;
                }
                break;
            case 3343801:
                if (value.equals("main")) {
                    c = 0;
                    break;
                }
                break;
            case 3530173:
                if (value.equals("sign")) {
                    c = '\b';
                    break;
                }
                break;
            case 552573414:
                if (value.equals("caption")) {
                    c = 6;
                    break;
                }
                break;
            case 899152809:
                if (value.equals("commentary")) {
                    c = 3;
                    break;
                }
                break;
            case 1629013393:
                if (value.equals("emergency")) {
                    c = 5;
                    break;
                }
                break;
            case 1855372047:
                if (value.equals("supplementary")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 8;
            case 4:
                return 16;
            case 5:
                return 32;
            case 6:
                return 64;
            case 7:
                return 128;
            case '\b':
                return 256;
            case '\t':
                return 512;
            case '\n':
                return 2048;
            default:
                return 0;
        }
    }

    protected int parseTvaAudioPurposeCsValue(String value) {
        if (value == null) {
            return 0;
        }
        char c = 65535;
        switch (value.hashCode()) {
            case 49:
                if (value.equals(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE)) {
                    c = 0;
                    break;
                }
                break;
            case 50:
                if (value.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                    c = 1;
                    break;
                }
                break;
            case 51:
                if (value.equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                    c = 2;
                    break;
                }
                break;
            case 52:
                if (value.equals("4")) {
                    c = 3;
                    break;
                }
                break;
            case 54:
                if (value.equals("6")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 512;
            case 1:
                return 2048;
            case 2:
                return 4;
            case 3:
                return 8;
            case 4:
                return 1;
            default:
                return 0;
        }
    }

    public static void maybeSkipTag(XmlPullParser xpp) throws IOException, XmlPullParserException {
        if (!XmlPullParserUtil.isStartTag(xpp)) {
            return;
        }
        int depth = 1;
        while (depth != 0) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp)) {
                depth++;
            } else if (XmlPullParserUtil.isEndTag(xpp)) {
                depth--;
            }
        }
    }

    private static void filterRedundantIncompleteSchemeDatas(ArrayList<DrmInitData.SchemeData> schemeDatas) {
        for (int i = schemeDatas.size() - 1; i >= 0; i--) {
            DrmInitData.SchemeData schemeData = schemeDatas.get(i);
            if (!schemeData.hasData()) {
                int j = 0;
                while (true) {
                    if (j < schemeDatas.size()) {
                        if (!schemeDatas.get(j).canReplace(schemeData)) {
                            j++;
                        } else {
                            schemeDatas.remove(i);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private static String getSampleMimeType(String containerMimeType, String codecs) {
        if (MimeTypes.isAudio(containerMimeType)) {
            return MimeTypes.getAudioMediaMimeType(codecs);
        }
        if (MimeTypes.isVideo(containerMimeType)) {
            return MimeTypes.getVideoMediaMimeType(codecs);
        }
        if (mimeTypeIsRawText(containerMimeType)) {
            return containerMimeType;
        }
        if (MimeTypes.APPLICATION_MP4.equals(containerMimeType)) {
            if (codecs != null) {
                if (codecs.startsWith(XMLSubtitleSampleEntry.TYPE)) {
                    return MimeTypes.APPLICATION_TTML;
                }
                if (codecs.startsWith(WebVTTSampleEntry.TYPE)) {
                    return MimeTypes.APPLICATION_MP4VTT;
                }
            }
        } else {
            if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType) && codecs != null) {
                if (codecs.contains("cea708")) {
                    return MimeTypes.APPLICATION_CEA708;
                }
                if (codecs.contains("eia608") || codecs.contains("cea608")) {
                    return MimeTypes.APPLICATION_CEA608;
                }
            }
            return null;
        }
        return null;
    }

    private static boolean mimeTypeIsRawText(String mimeType) {
        return MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType) || MimeTypes.APPLICATION_MP4VTT.equals(mimeType) || MimeTypes.APPLICATION_CEA708.equals(mimeType) || MimeTypes.APPLICATION_CEA608.equals(mimeType);
    }

    private static String checkLanguageConsistency(String firstLanguage, String secondLanguage) {
        if (firstLanguage == null) {
            return secondLanguage;
        }
        if (secondLanguage == null) {
            return firstLanguage;
        }
        Assertions.checkState(firstLanguage.equals(secondLanguage));
        return firstLanguage;
    }

    private static int checkContentTypeConsistency(int firstType, int secondType) {
        if (firstType == -1) {
            return secondType;
        }
        if (secondType == -1) {
            return firstType;
        }
        Assertions.checkState(firstType == secondType);
        return firstType;
    }

    protected static Descriptor parseDescriptor(XmlPullParser xpp, String tag) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", "");
        String value = parseString(xpp, CommonProperties.VALUE, null);
        String id = parseString(xpp, "id", null);
        do {
            xpp.next();
        } while (!XmlPullParserUtil.isEndTag(xpp, tag));
        return new Descriptor(schemeIdUri, value, id);
    }

    protected static int parseCea608AccessibilityChannel(List<Descriptor> accessibilityDescriptors) {
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = accessibilityDescriptors.get(i);
            if ("urn:scte:dash:cc:cea-608:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher accessibilityValueMatcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (accessibilityValueMatcher.matches()) {
                    return Integer.parseInt(accessibilityValueMatcher.group(1));
                }
                Log.w(TAG, "Unable to parse CEA-608 channel number from: " + descriptor.value);
            }
        }
        return -1;
    }

    protected static int parseCea708AccessibilityChannel(List<Descriptor> accessibilityDescriptors) {
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = accessibilityDescriptors.get(i);
            if ("urn:scte:dash:cc:cea-708:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher accessibilityValueMatcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (accessibilityValueMatcher.matches()) {
                    return Integer.parseInt(accessibilityValueMatcher.group(1));
                }
                Log.w(TAG, "Unable to parse CEA-708 service block number from: " + descriptor.value);
            }
        }
        return -1;
    }

    protected static String parseEac3SupplementalProperties(List<Descriptor> supplementalProperties) {
        for (int i = 0; i < supplementalProperties.size(); i++) {
            Descriptor descriptor = supplementalProperties.get(i);
            String schemeIdUri = descriptor.schemeIdUri;
            if (!"tag:dolby.com,2018:dash:EC3_ExtensionType:2018".equals(schemeIdUri) || !"JOC".equals(descriptor.value)) {
                if ("tag:dolby.com,2014:dash:DolbyDigitalPlusExtensionType:2014".equals(schemeIdUri) && "ec+3".equals(descriptor.value)) {
                    return MimeTypes.AUDIO_E_AC3_JOC;
                }
            } else {
                return MimeTypes.AUDIO_E_AC3_JOC;
            }
        }
        return MimeTypes.AUDIO_E_AC3;
    }

    protected static float parseFrameRate(XmlPullParser xpp, float defaultValue) {
        String frameRateAttribute = xpp.getAttributeValue(null, "frameRate");
        if (frameRateAttribute == null) {
            return defaultValue;
        }
        Matcher frameRateMatcher = FRAME_RATE_PATTERN.matcher(frameRateAttribute);
        if (!frameRateMatcher.matches()) {
            return defaultValue;
        }
        int numerator = Integer.parseInt(frameRateMatcher.group(1));
        String denominatorString = frameRateMatcher.group(2);
        if (!TextUtils.isEmpty(denominatorString)) {
            float frameRate = numerator / Integer.parseInt(denominatorString);
            return frameRate;
        }
        float frameRate2 = numerator;
        return frameRate2;
    }

    protected static long parseDuration(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        if (value == null) {
            return defaultValue;
        }
        return Util.parseXsDuration(value);
    }

    protected static long parseDateTime(XmlPullParser xpp, String name, long defaultValue) throws ParserException {
        String value = xpp.getAttributeValue(null, name);
        if (value == null) {
            return defaultValue;
        }
        return Util.parseXsDateTime(value);
    }

    protected static String parseText(XmlPullParser xpp, String label) throws XmlPullParserException, IOException {
        String text = "";
        do {
            xpp.next();
            if (xpp.getEventType() == 4) {
                text = xpp.getText();
            } else {
                maybeSkipTag(xpp);
            }
        } while (!XmlPullParserUtil.isEndTag(xpp, label));
        return text;
    }

    protected static int parseInt(XmlPullParser xpp, String name, int defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    protected static long parseLong(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    protected static String parseString(XmlPullParser xpp, String name, String defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : value;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    protected static int parseDolbyChannelConfiguration(XmlPullParser xpp) {
        char c;
        String value = Util.toLowerInvariant(xpp.getAttributeValue(null, CommonProperties.VALUE));
        if (value == null) {
            return -1;
        }
        switch (value.hashCode()) {
            case 1596796:
                if (value.equals("4000")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 2937391:
                if (value.equals("a000")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3094035:
                if (value.equals("f801")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3133436:
                if (value.equals("fa01")) {
                    c = 3;
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
                return 1;
            case 1:
                return 2;
            case 2:
                return 6;
            case 3:
                return 8;
            default:
                return -1;
        }
    }

    protected static long parseLastSegmentNumberSupplementalProperty(List<Descriptor> supplementalProperties) {
        for (int i = 0; i < supplementalProperties.size(); i++) {
            Descriptor descriptor = supplementalProperties.get(i);
            if ("http://dashif.org/guidelines/last-segment-number".equalsIgnoreCase(descriptor.schemeIdUri)) {
                return Long.parseLong(descriptor.value);
            }
        }
        return -1L;
    }

    /* loaded from: classes3.dex */
    public static final class RepresentationInfo {
        public final String baseUrl;
        public final ArrayList<DrmInitData.SchemeData> drmSchemeDatas;
        public final String drmSchemeType;
        public final Format format;
        public final ArrayList<Descriptor> inbandEventStreams;
        public final long revisionId;
        public final SegmentBase segmentBase;

        public RepresentationInfo(Format format, String baseUrl, SegmentBase segmentBase, String drmSchemeType, ArrayList<DrmInitData.SchemeData> drmSchemeDatas, ArrayList<Descriptor> inbandEventStreams, long revisionId) {
            this.format = format;
            this.baseUrl = baseUrl;
            this.segmentBase = segmentBase;
            this.drmSchemeType = drmSchemeType;
            this.drmSchemeDatas = drmSchemeDatas;
            this.inbandEventStreams = inbandEventStreams;
            this.revisionId = revisionId;
        }
    }
}
