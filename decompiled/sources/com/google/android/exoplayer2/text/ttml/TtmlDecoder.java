package com.google.android.exoplayer2.text.ttml;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
/* loaded from: classes3.dex */
public final class TtmlDecoder extends SimpleSubtitleDecoder {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_IMAGE = "backgroundImage";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final String TAG = "TtmlDecoder";
    private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
    private final XmlPullParserFactory xmlParserFactory;
    private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
    private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
    private static final Pattern FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    private static final Pattern PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    private static final Pattern PIXEL_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)px (\\d+\\.?\\d*?)px$");
    private static final Pattern CELL_RESOLUTION = Pattern.compile("^(\\d+) (\\d+)$");
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0f, 1, 1);
    private static final CellResolution DEFAULT_CELL_RESOLUTION = new CellResolution(32, 15);

    public TtmlDecoder() {
        super(TAG);
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            this.xmlParserFactory = newInstance;
            newInstance.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    @Override // com.google.android.exoplayer2.text.SimpleSubtitleDecoder
    protected Subtitle decode(byte[] bytes, int length, boolean reset) throws SubtitleDecoderException {
        ByteArrayInputStream inputStream;
        ArrayDeque<TtmlNode> nodeStack;
        Map<String, TtmlStyle> globalStyles;
        TtsExtent ttsExtent;
        CellResolution cellResolution;
        FrameAndTickRate frameAndTickRate;
        Map<String, TtmlStyle> globalStyles2;
        FrameAndTickRate frameAndTickRate2;
        try {
            XmlPullParser xmlParser = this.xmlParserFactory.newPullParser();
            Map<String, TtmlStyle> globalStyles3 = new HashMap<>();
            Map<String, TtmlRegion> regionMap = new HashMap<>();
            Map<String, String> imageMap = new HashMap<>();
            regionMap.put("", new TtmlRegion(null));
            ByteArrayInputStream inputStream2 = new ByteArrayInputStream(bytes, 0, length);
            xmlParser.setInput(inputStream2, null);
            ArrayDeque<TtmlNode> nodeStack2 = new ArrayDeque<>();
            int eventType = xmlParser.getEventType();
            FrameAndTickRate frameAndTickRate3 = DEFAULT_FRAME_AND_TICK_RATE;
            CellResolution cellResolution2 = DEFAULT_CELL_RESOLUTION;
            TtsExtent ttsExtent2 = null;
            TtmlSubtitle ttmlSubtitle = null;
            int unsupportedNodeDepth = 0;
            int eventType2 = eventType;
            while (eventType2 != 1) {
                TtmlNode parent = nodeStack2.peek();
                if (unsupportedNodeDepth != 0) {
                    globalStyles = globalStyles3;
                    inputStream = inputStream2;
                    int eventType3 = eventType2;
                    nodeStack = nodeStack2;
                    if (eventType3 == 2) {
                        unsupportedNodeDepth++;
                    } else if (eventType3 == 3) {
                        unsupportedNodeDepth--;
                    }
                } else {
                    String name = xmlParser.getName();
                    if (eventType2 == 2) {
                        if (!TtmlNode.TAG_TT.equals(name)) {
                            cellResolution = cellResolution2;
                            ttsExtent = ttsExtent2;
                            frameAndTickRate = frameAndTickRate3;
                        } else {
                            FrameAndTickRate frameAndTickRate4 = parseFrameAndTickRates(xmlParser);
                            CellResolution cellResolution3 = parseCellResolution(xmlParser, DEFAULT_CELL_RESOLUTION);
                            TtsExtent ttsExtent3 = parseTtsExtent(xmlParser);
                            cellResolution = cellResolution3;
                            ttsExtent = ttsExtent3;
                            frameAndTickRate = frameAndTickRate4;
                        }
                        if (isSupportedTag(name)) {
                            if (TtmlNode.TAG_HEAD.equals(name)) {
                                frameAndTickRate2 = frameAndTickRate;
                                inputStream = inputStream2;
                                globalStyles2 = globalStyles3;
                                nodeStack = nodeStack2;
                                parseHeader(xmlParser, globalStyles3, cellResolution, ttsExtent, regionMap, imageMap);
                            } else {
                                frameAndTickRate2 = frameAndTickRate;
                                globalStyles2 = globalStyles3;
                                inputStream = inputStream2;
                                nodeStack = nodeStack2;
                                try {
                                    TtmlNode node = parseNode(xmlParser, parent, regionMap, frameAndTickRate2);
                                    nodeStack.push(node);
                                    if (parent != null) {
                                        parent.addChild(node);
                                    }
                                } catch (SubtitleDecoderException e) {
                                    Log.w(TAG, "Suppressing parser error", e);
                                    unsupportedNodeDepth++;
                                    frameAndTickRate3 = frameAndTickRate2;
                                    cellResolution2 = cellResolution;
                                    ttsExtent2 = ttsExtent;
                                    globalStyles = globalStyles2;
                                }
                            }
                            frameAndTickRate3 = frameAndTickRate2;
                            cellResolution2 = cellResolution;
                            ttsExtent2 = ttsExtent;
                            globalStyles = globalStyles2;
                        } else {
                            Log.i(TAG, "Ignoring unsupported tag: " + xmlParser.getName());
                            unsupportedNodeDepth++;
                            frameAndTickRate3 = frameAndTickRate;
                            globalStyles = globalStyles3;
                            inputStream = inputStream2;
                            cellResolution2 = cellResolution;
                            ttsExtent2 = ttsExtent;
                            nodeStack = nodeStack2;
                        }
                    } else {
                        Map<String, TtmlStyle> globalStyles4 = globalStyles3;
                        inputStream = inputStream2;
                        int eventType4 = eventType2;
                        nodeStack = nodeStack2;
                        if (eventType4 == 4) {
                            parent.addChild(TtmlNode.buildTextNode(xmlParser.getText()));
                            globalStyles = globalStyles4;
                        } else if (eventType4 == 3) {
                            if (!xmlParser.getName().equals(TtmlNode.TAG_TT)) {
                                globalStyles = globalStyles4;
                            } else {
                                globalStyles = globalStyles4;
                                ttmlSubtitle = new TtmlSubtitle(nodeStack.peek(), globalStyles, regionMap, imageMap);
                            }
                            nodeStack.pop();
                        } else {
                            globalStyles = globalStyles4;
                        }
                    }
                }
                xmlParser.next();
                eventType2 = xmlParser.getEventType();
                nodeStack2 = nodeStack;
                inputStream2 = inputStream;
                globalStyles3 = globalStyles;
            }
            return ttmlSubtitle;
        } catch (IOException e2) {
            throw new IllegalStateException("Unexpected error when reading input.", e2);
        } catch (XmlPullParserException xppe) {
            throw new SubtitleDecoderException("Unable to decode source", xppe);
        }
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlParser) throws SubtitleDecoderException {
        int frameRate = 30;
        String frameRateString = xmlParser.getAttributeValue(TTP, "frameRate");
        if (frameRateString != null) {
            frameRate = Integer.parseInt(frameRateString);
        }
        float frameRateMultiplier = 1.0f;
        String frameRateMultiplierString = xmlParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (frameRateMultiplierString != null) {
            String[] parts = Util.split(frameRateMultiplierString, " ");
            if (parts.length != 2) {
                throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
            }
            float numerator = Integer.parseInt(parts[0]);
            float denominator = Integer.parseInt(parts[1]);
            frameRateMultiplier = numerator / denominator;
        }
        FrameAndTickRate frameAndTickRate = DEFAULT_FRAME_AND_TICK_RATE;
        int subFrameRate = frameAndTickRate.subFrameRate;
        String subFrameRateString = xmlParser.getAttributeValue(TTP, "subFrameRate");
        if (subFrameRateString != null) {
            subFrameRate = Integer.parseInt(subFrameRateString);
        }
        int tickRate = frameAndTickRate.tickRate;
        String tickRateString = xmlParser.getAttributeValue(TTP, "tickRate");
        if (tickRateString != null) {
            tickRate = Integer.parseInt(tickRateString);
        }
        return new FrameAndTickRate(frameRate * frameRateMultiplier, subFrameRate, tickRate);
    }

    private CellResolution parseCellResolution(XmlPullParser xmlParser, CellResolution defaultValue) throws SubtitleDecoderException {
        String cellResolution = xmlParser.getAttributeValue(TTP, "cellResolution");
        if (cellResolution == null) {
            return defaultValue;
        }
        Matcher cellResolutionMatcher = CELL_RESOLUTION.matcher(cellResolution);
        if (!cellResolutionMatcher.matches()) {
            Log.w(TAG, "Ignoring malformed cell resolution: " + cellResolution);
            return defaultValue;
        }
        try {
            int columns = Integer.parseInt(cellResolutionMatcher.group(1));
            int rows = Integer.parseInt(cellResolutionMatcher.group(2));
            if (columns == 0 || rows == 0) {
                throw new SubtitleDecoderException("Invalid cell resolution " + columns + " " + rows);
            }
            return new CellResolution(columns, rows);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Ignoring malformed cell resolution: " + cellResolution);
            return defaultValue;
        }
    }

    private TtsExtent parseTtsExtent(XmlPullParser xmlParser) {
        String ttsExtent = XmlPullParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_EXTENT);
        if (ttsExtent == null) {
            return null;
        }
        Matcher extentMatcher = PIXEL_COORDINATES.matcher(ttsExtent);
        if (!extentMatcher.matches()) {
            Log.w(TAG, "Ignoring non-pixel tts extent: " + ttsExtent);
            return null;
        }
        try {
            int width = Integer.parseInt(extentMatcher.group(1));
            int height = Integer.parseInt(extentMatcher.group(2));
            return new TtsExtent(width, height);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Ignoring malformed tts extent: " + ttsExtent);
            return null;
        }
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlParser, Map<String, TtmlStyle> globalStyles, CellResolution cellResolution, TtsExtent ttsExtent, Map<String, TtmlRegion> globalRegions, Map<String, String> imageMap) throws IOException, XmlPullParserException {
        String[] parseStyleIds;
        do {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, "style")) {
                String parentStyleId = XmlPullParserUtil.getAttributeValue(xmlParser, "style");
                TtmlStyle style = parseStyleAttributes(xmlParser, new TtmlStyle());
                if (parentStyleId != null) {
                    for (String id : parseStyleIds(parentStyleId)) {
                        style.chain(globalStyles.get(id));
                    }
                }
                if (style.getId() != null) {
                    globalStyles.put(style.getId(), style);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, "region")) {
                TtmlRegion ttmlRegion = parseRegionAttributes(xmlParser, cellResolution, ttsExtent);
                if (ttmlRegion != null) {
                    globalRegions.put(ttmlRegion.id, ttmlRegion);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, TtmlNode.TAG_METADATA)) {
                parseMetadata(xmlParser, imageMap);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlParser, TtmlNode.TAG_HEAD));
        return globalStyles;
    }

    private void parseMetadata(XmlPullParser xmlParser, Map<String, String> imageMap) throws IOException, XmlPullParserException {
        String id;
        do {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, TtmlNode.TAG_IMAGE) && (id = XmlPullParserUtil.getAttributeValue(xmlParser, "id")) != null) {
                String encodedBitmapData = xmlParser.nextText();
                imageMap.put(id, encodedBitmapData);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlParser, TtmlNode.TAG_METADATA));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0135, code lost:
        if (r4.equals("after") != false) goto L47;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.google.android.exoplayer2.text.ttml.TtmlRegion parseRegionAttributes(org.xmlpull.v1.XmlPullParser r24, com.google.android.exoplayer2.text.ttml.TtmlDecoder.CellResolution r25, com.google.android.exoplayer2.text.ttml.TtmlDecoder.TtsExtent r26) {
        /*
            Method dump skipped, instructions count: 500
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseRegionAttributes(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlDecoder$CellResolution, com.google.android.exoplayer2.text.ttml.TtmlDecoder$TtsExtent):com.google.android.exoplayer2.text.ttml.TtmlRegion");
    }

    private String[] parseStyleIds(String parentStyleIds) {
        String parentStyleIds2 = parentStyleIds.trim();
        return parentStyleIds2.isEmpty() ? new String[0] : Util.split(parentStyleIds2, "\\s+");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00a6, code lost:
        if (r3.equals(com.google.android.exoplayer2.text.ttml.TtmlNode.UNDERLINE) != false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x0118, code lost:
        if (r3.equals(com.google.android.exoplayer2.text.ttml.TtmlNode.CENTER) != false) goto L72;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.google.android.exoplayer2.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r12, com.google.android.exoplayer2.text.ttml.TtmlStyle r13) {
        /*
            Method dump skipped, instructions count: 638
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlStyle):com.google.android.exoplayer2.text.ttml.TtmlStyle");
    }

    private TtmlStyle createIfNull(TtmlStyle style) {
        return style == null ? new TtmlStyle() : style;
    }

    private TtmlNode parseNode(XmlPullParser parser, TtmlNode parent, Map<String, TtmlRegion> regionMap, FrameAndTickRate frameAndTickRate) throws SubtitleDecoderException {
        String regionId;
        TtmlDecoder ttmlDecoder = this;
        XmlPullParser xmlPullParser = parser;
        long duration = C.TIME_UNSET;
        long startTime = C.TIME_UNSET;
        long endTime = C.TIME_UNSET;
        String regionId2 = "";
        String value = null;
        String[] styleIds = null;
        int attributeCount = parser.getAttributeCount();
        TtmlStyle style = ttmlDecoder.parseStyleAttributes(xmlPullParser, null);
        int i = 0;
        while (i < attributeCount) {
            int attributeCount2 = attributeCount;
            String attr = xmlPullParser.getAttributeName(i);
            String imageId = value;
            String value2 = xmlPullParser.getAttributeValue(i);
            char c = 65535;
            switch (attr.hashCode()) {
                case -934795532:
                    if (attr.equals("region")) {
                        c = 4;
                        break;
                    }
                    break;
                case 99841:
                    if (attr.equals(ATTR_DURATION)) {
                        c = 2;
                        break;
                    }
                    break;
                case 100571:
                    if (attr.equals("end")) {
                        c = 1;
                        break;
                    }
                    break;
                case 93616297:
                    if (attr.equals(ATTR_BEGIN)) {
                        c = 0;
                        break;
                    }
                    break;
                case 109780401:
                    if (attr.equals("style")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1292595405:
                    if (attr.equals(ATTR_IMAGE)) {
                        c = 5;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    long startTime2 = parseTimeExpression(value2, frameAndTickRate);
                    startTime = startTime2;
                    value = imageId;
                    continue;
                    i++;
                    ttmlDecoder = this;
                    xmlPullParser = parser;
                    attributeCount = attributeCount2;
                case 1:
                    long endTime2 = parseTimeExpression(value2, frameAndTickRate);
                    endTime = endTime2;
                    value = imageId;
                    continue;
                    i++;
                    ttmlDecoder = this;
                    xmlPullParser = parser;
                    attributeCount = attributeCount2;
                case 2:
                    long duration2 = parseTimeExpression(value2, frameAndTickRate);
                    duration = duration2;
                    value = imageId;
                    continue;
                    i++;
                    ttmlDecoder = this;
                    xmlPullParser = parser;
                    attributeCount = attributeCount2;
                case 3:
                    String[] ids = ttmlDecoder.parseStyleIds(value2);
                    if (ids.length > 0) {
                        styleIds = ids;
                        value = imageId;
                        continue;
                        i++;
                        ttmlDecoder = this;
                        xmlPullParser = parser;
                        attributeCount = attributeCount2;
                    }
                    break;
                case 4:
                    if (regionMap.containsKey(value2)) {
                        regionId2 = value2;
                        value = imageId;
                        continue;
                        i++;
                        ttmlDecoder = this;
                        xmlPullParser = parser;
                        attributeCount = attributeCount2;
                    }
                    break;
                case 5:
                    if (value2.startsWith("#")) {
                        value = value2.substring(1);
                        continue;
                        i++;
                        ttmlDecoder = this;
                        xmlPullParser = parser;
                        attributeCount = attributeCount2;
                    }
                    break;
            }
            value = imageId;
            i++;
            ttmlDecoder = this;
            xmlPullParser = parser;
            attributeCount = attributeCount2;
        }
        String imageId2 = value;
        if (parent != null) {
            regionId = regionId2;
            if (parent.startTimeUs != C.TIME_UNSET) {
                if (startTime != C.TIME_UNSET) {
                    startTime += parent.startTimeUs;
                }
                if (endTime != C.TIME_UNSET) {
                    endTime += parent.startTimeUs;
                }
            }
        } else {
            regionId = regionId2;
        }
        if (endTime == C.TIME_UNSET) {
            if (duration != C.TIME_UNSET) {
                endTime = startTime + duration;
            } else if (parent != null && parent.endTimeUs != C.TIME_UNSET) {
                endTime = parent.endTimeUs;
            }
        }
        return TtmlNode.buildNode(parser.getName(), startTime, endTime, style, styleIds, regionId, imageId2);
    }

    private static boolean isSupportedTag(String tag) {
        return tag.equals(TtmlNode.TAG_TT) || tag.equals(TtmlNode.TAG_HEAD) || tag.equals(TtmlNode.TAG_BODY) || tag.equals(TtmlNode.TAG_DIV) || tag.equals(TtmlNode.TAG_P) || tag.equals(TtmlNode.TAG_SPAN) || tag.equals(TtmlNode.TAG_BR) || tag.equals("style") || tag.equals(TtmlNode.TAG_STYLING) || tag.equals(TtmlNode.TAG_LAYOUT) || tag.equals("region") || tag.equals(TtmlNode.TAG_METADATA) || tag.equals(TtmlNode.TAG_IMAGE) || tag.equals("data") || tag.equals(TtmlNode.TAG_INFORMATION);
    }

    private static void parseFontSize(String expression, TtmlStyle out) throws SubtitleDecoderException {
        Matcher matcher;
        String[] expressions = Util.split(expression, "\\s+");
        if (expressions.length == 1) {
            matcher = FONT_SIZE.matcher(expression);
        } else if (expressions.length == 2) {
            matcher = FONT_SIZE.matcher(expressions[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            throw new SubtitleDecoderException("Invalid number of entries for fontSize: " + expressions.length + ".");
        }
        if (matcher.matches()) {
            String unit = matcher.group(3);
            char c = 65535;
            switch (unit.hashCode()) {
                case 37:
                    if (unit.equals("%")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3240:
                    if (unit.equals("em")) {
                        c = 1;
                        break;
                    }
                    break;
                case 3592:
                    if (unit.equals("px")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    out.setFontSizeUnit(1);
                    break;
                case 1:
                    out.setFontSizeUnit(2);
                    break;
                case 2:
                    out.setFontSizeUnit(3);
                    break;
                default:
                    throw new SubtitleDecoderException("Invalid unit for fontSize: '" + unit + "'.");
            }
            out.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        throw new SubtitleDecoderException("Invalid expression for fontSize: '" + expression + "'.");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00bf, code lost:
        if (r11.equals(org.telegram.ui.ActionBar.Theme.THEME_BACKGROUND_SLUG) != false) goto L39;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static long parseTimeExpression(java.lang.String r16, com.google.android.exoplayer2.text.ttml.TtmlDecoder.FrameAndTickRate r17) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /*
            Method dump skipped, instructions count: 352
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseTimeExpression(java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlDecoder$FrameAndTickRate):long");
    }

    /* loaded from: classes3.dex */
    public static final class FrameAndTickRate {
        final float effectiveFrameRate;
        final int subFrameRate;
        final int tickRate;

        FrameAndTickRate(float effectiveFrameRate, int subFrameRate, int tickRate) {
            this.effectiveFrameRate = effectiveFrameRate;
            this.subFrameRate = subFrameRate;
            this.tickRate = tickRate;
        }
    }

    /* loaded from: classes3.dex */
    public static final class CellResolution {
        final int columns;
        final int rows;

        CellResolution(int columns, int rows) {
            this.columns = columns;
            this.rows = rows;
        }
    }

    /* loaded from: classes3.dex */
    public static final class TtsExtent {
        final int height;
        final int width;

        TtsExtent(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
