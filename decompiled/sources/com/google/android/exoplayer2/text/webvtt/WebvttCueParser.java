package com.google.android.exoplayer2.text.webvtt;

import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.text.webvtt.WebvttCue;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.location.LocationRequest;
import com.microsoft.appcenter.Constants;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes3.dex */
public final class WebvttCueParser {
    private static final char CHAR_AMPERSAND = '&';
    private static final char CHAR_GREATER_THAN = '>';
    private static final char CHAR_LESS_THAN = '<';
    private static final char CHAR_SEMI_COLON = ';';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_SPACE = ' ';
    public static final Pattern CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
    private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
    private static final String ENTITY_AMPERSAND = "amp";
    private static final String ENTITY_GREATER_THAN = "gt";
    private static final String ENTITY_LESS_THAN = "lt";
    private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;
    private static final String TAG = "WebvttCueParser";
    private static final String TAG_BOLD = "b";
    private static final String TAG_CLASS = "c";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_LANG = "lang";
    private static final String TAG_UNDERLINE = "u";
    private static final String TAG_VOICE = "v";
    private final StringBuilder textBuilder = new StringBuilder();

    public boolean parseCue(ParsableByteArray webvttData, WebvttCue.Builder builder, List<WebvttCssStyle> styles) {
        String firstLine = webvttData.readLine();
        if (firstLine == null) {
            return false;
        }
        Pattern pattern = CUE_HEADER_PATTERN;
        Matcher cueHeaderMatcher = pattern.matcher(firstLine);
        if (cueHeaderMatcher.matches()) {
            return parseCue(null, cueHeaderMatcher, webvttData, builder, this.textBuilder, styles);
        }
        String secondLine = webvttData.readLine();
        if (secondLine == null) {
            return false;
        }
        Matcher cueHeaderMatcher2 = pattern.matcher(secondLine);
        if (cueHeaderMatcher2.matches()) {
            return parseCue(firstLine.trim(), cueHeaderMatcher2, webvttData, builder, this.textBuilder, styles);
        }
        return false;
    }

    public static void parseCueSettingsList(String cueSettingsList, WebvttCue.Builder builder) {
        Matcher cueSettingMatcher = CUE_SETTING_PATTERN.matcher(cueSettingsList);
        while (cueSettingMatcher.find()) {
            String name = cueSettingMatcher.group(1);
            String value = cueSettingMatcher.group(2);
            try {
                if ("line".equals(name)) {
                    parseLineAttribute(value, builder);
                } else if ("align".equals(name)) {
                    builder.setTextAlignment(parseTextAlignment(value));
                } else if ("position".equals(name)) {
                    parsePositionAttribute(value, builder);
                } else if ("size".equals(name)) {
                    builder.setWidth(WebvttParserUtil.parsePercentage(value));
                } else {
                    Log.w(TAG, "Unknown cue setting " + name + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + value);
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Skipping bad cue setting: " + cueSettingMatcher.group());
            }
        }
    }

    public static void parseCueText(String id, String markup, WebvttCue.Builder builder, List<WebvttCssStyle> styles) {
        int entityEndIndex;
        SpannableStringBuilder spannedText = new SpannableStringBuilder();
        ArrayDeque<StartTag> startTagStack = new ArrayDeque<>();
        List<StyleMatch> scratchStyleMatches = new ArrayList<>();
        int pos = 0;
        while (pos < markup.length()) {
            char curr = markup.charAt(pos);
            switch (curr) {
                case '&':
                    int semiColonEndIndex = markup.indexOf(59, pos + 1);
                    int spaceEndIndex = markup.indexOf(32, pos + 1);
                    if (semiColonEndIndex == -1) {
                        entityEndIndex = spaceEndIndex;
                    } else {
                        entityEndIndex = spaceEndIndex == -1 ? semiColonEndIndex : Math.min(semiColonEndIndex, spaceEndIndex);
                    }
                    if (entityEndIndex != -1) {
                        applyEntity(markup.substring(pos + 1, entityEndIndex), spannedText);
                        if (entityEndIndex == spaceEndIndex) {
                            spannedText.append((CharSequence) " ");
                        }
                        pos = entityEndIndex + 1;
                        break;
                    } else {
                        spannedText.append(curr);
                        pos++;
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (pos + 1 >= markup.length()) {
                        pos++;
                        break;
                    } else {
                        int ltPos = pos;
                        boolean isVoidTag = false;
                        int i = 1;
                        boolean isClosingTag = markup.charAt(ltPos + 1) == '/';
                        pos = findEndOfTag(markup, ltPos + 1);
                        if (markup.charAt(pos - 2) == '/') {
                            isVoidTag = true;
                        }
                        if (isClosingTag) {
                            i = 2;
                        }
                        String fullTagExpression = markup.substring(i + ltPos, isVoidTag ? pos - 2 : pos - 1);
                        if (!fullTagExpression.trim().isEmpty()) {
                            String tagName = getTagName(fullTagExpression);
                            if (!isSupportedTag(tagName)) {
                                break;
                            } else if (isClosingTag) {
                                while (!startTagStack.isEmpty()) {
                                    StartTag startTag = startTagStack.pop();
                                    applySpansForTag(id, startTag, spannedText, styles, scratchStyleMatches);
                                    if (startTag.name.equals(tagName)) {
                                        break;
                                    }
                                }
                                break;
                            } else if (isVoidTag) {
                                break;
                            } else {
                                startTagStack.push(StartTag.buildStartTag(fullTagExpression, spannedText.length()));
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    break;
                default:
                    spannedText.append(curr);
                    pos++;
                    break;
            }
        }
        while (!startTagStack.isEmpty()) {
            applySpansForTag(id, startTagStack.pop(), spannedText, styles, scratchStyleMatches);
        }
        applySpansForTag(id, StartTag.buildWholeCueVirtualTag(), spannedText, styles, scratchStyleMatches);
        builder.setText(spannedText);
    }

    private static boolean parseCue(String id, Matcher cueHeaderMatcher, ParsableByteArray webvttData, WebvttCue.Builder builder, StringBuilder textBuilder, List<WebvttCssStyle> styles) {
        try {
            builder.setStartTime(WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(1))).setEndTime(WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(2)));
            parseCueSettingsList(cueHeaderMatcher.group(3), builder);
            textBuilder.setLength(0);
            String line = webvttData.readLine();
            while (!TextUtils.isEmpty(line)) {
                if (textBuilder.length() > 0) {
                    textBuilder.append("\n");
                }
                textBuilder.append(line.trim());
                line = webvttData.readLine();
            }
            String line2 = textBuilder.toString();
            parseCueText(id, line2, builder, styles);
            return true;
        } catch (NumberFormatException e) {
            Log.w(TAG, "Skipping cue with bad header: " + cueHeaderMatcher.group());
            return false;
        }
    }

    private static void parseLineAttribute(String s, WebvttCue.Builder builder) {
        int commaIndex = s.indexOf(44);
        if (commaIndex != -1) {
            builder.setLineAnchor(parsePositionAnchor(s.substring(commaIndex + 1)));
            s = s.substring(0, commaIndex);
        }
        if (s.endsWith("%")) {
            builder.setLine(WebvttParserUtil.parsePercentage(s)).setLineType(0);
            return;
        }
        int lineNumber = Integer.parseInt(s);
        if (lineNumber < 0) {
            lineNumber--;
        }
        builder.setLine(lineNumber).setLineType(1);
    }

    private static void parsePositionAttribute(String s, WebvttCue.Builder builder) {
        int commaIndex = s.indexOf(44);
        if (commaIndex != -1) {
            builder.setPositionAnchor(parsePositionAnchor(s.substring(commaIndex + 1)));
            s = s.substring(0, commaIndex);
        }
        builder.setPosition(WebvttParserUtil.parsePercentage(s));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int parsePositionAnchor(String s) {
        char c;
        switch (s.hashCode()) {
            case -1364013995:
                if (s.equals(TtmlNode.CENTER)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1074341483:
                if (s.equals("middle")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 100571:
                if (s.equals(TtmlNode.END)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 109757538:
                if (s.equals(TtmlNode.START)) {
                    c = 0;
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
                return 0;
            case 1:
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                Log.w(TAG, "Invalid anchor value: " + s);
                return Integer.MIN_VALUE;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int parseTextAlignment(String s) {
        char c;
        switch (s.hashCode()) {
            case -1364013995:
                if (s.equals(TtmlNode.CENTER)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1074341483:
                if (s.equals("middle")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 100571:
                if (s.equals(TtmlNode.END)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 3317767:
                if (s.equals(TtmlNode.LEFT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 108511772:
                if (s.equals(TtmlNode.RIGHT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 109757538:
                if (s.equals(TtmlNode.START)) {
                    c = 0;
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
                return 4;
            case 2:
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 5;
            default:
                Log.w(TAG, "Invalid alignment value: " + s);
                return 2;
        }
    }

    private static int findEndOfTag(String markup, int startPos) {
        int index = markup.indexOf(62, startPos);
        return index == -1 ? markup.length() : index + 1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static void applyEntity(String entity, SpannableStringBuilder spannedText) {
        char c;
        switch (entity.hashCode()) {
            case 3309:
                if (entity.equals(ENTITY_GREATER_THAN)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3464:
                if (entity.equals(ENTITY_LESS_THAN)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 96708:
                if (entity.equals(ENTITY_AMPERSAND)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 3374865:
                if (entity.equals(ENTITY_NON_BREAK_SPACE)) {
                    c = 2;
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
                spannedText.append(CHAR_LESS_THAN);
                return;
            case 1:
                spannedText.append(CHAR_GREATER_THAN);
                return;
            case 2:
                spannedText.append(CHAR_SPACE);
                return;
            case 3:
                spannedText.append(CHAR_AMPERSAND);
                return;
            default:
                Log.w(TAG, "ignoring unsupported entity: '&" + entity + ";'");
                return;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static boolean isSupportedTag(String tagName) {
        char c;
        switch (tagName.hashCode()) {
            case 98:
                if (tagName.equals(TAG_BOLD)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 99:
                if (tagName.equals("c")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case LocationRequest.PRIORITY_NO_POWER /* 105 */:
                if (tagName.equals(TAG_ITALIC)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 117:
                if (tagName.equals(TAG_UNDERLINE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 118:
                if (tagName.equals(TAG_VOICE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 3314158:
                if (tagName.equals(TAG_LANG)) {
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
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                return false;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static void applySpansForTag(String cueId, StartTag startTag, SpannableStringBuilder text, List<WebvttCssStyle> styles, List<StyleMatch> scratchStyleMatches) {
        char c;
        int start = startTag.position;
        int end = text.length();
        String str = startTag.name;
        switch (str.hashCode()) {
            case 0:
                if (str.equals("")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 98:
                if (str.equals(TAG_BOLD)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 99:
                if (str.equals("c")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case LocationRequest.PRIORITY_NO_POWER /* 105 */:
                if (str.equals(TAG_ITALIC)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 117:
                if (str.equals(TAG_UNDERLINE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 118:
                if (str.equals(TAG_VOICE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 3314158:
                if (str.equals(TAG_LANG)) {
                    c = 4;
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
                text.setSpan(new StyleSpan(1), start, end, 33);
                break;
            case 1:
                text.setSpan(new StyleSpan(2), start, end, 33);
                break;
            case 2:
                text.setSpan(new UnderlineSpan(), start, end, 33);
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                break;
            default:
                return;
        }
        scratchStyleMatches.clear();
        getApplicableStyles(styles, cueId, startTag, scratchStyleMatches);
        int styleMatchesCount = scratchStyleMatches.size();
        for (int i = 0; i < styleMatchesCount; i++) {
            applyStyleToText(text, scratchStyleMatches.get(i).style, start, end);
        }
    }

    private static void applyStyleToText(SpannableStringBuilder spannedText, WebvttCssStyle style, int start, int end) {
        if (style == null) {
            return;
        }
        if (style.getStyle() != -1) {
            spannedText.setSpan(new StyleSpan(style.getStyle()), start, end, 33);
        }
        if (style.isLinethrough()) {
            spannedText.setSpan(new StrikethroughSpan(), start, end, 33);
        }
        if (style.isUnderline()) {
            spannedText.setSpan(new UnderlineSpan(), start, end, 33);
        }
        if (style.hasFontColor()) {
            spannedText.setSpan(new ForegroundColorSpan(style.getFontColor()), start, end, 33);
        }
        if (style.hasBackgroundColor()) {
            spannedText.setSpan(new BackgroundColorSpan(style.getBackgroundColor()), start, end, 33);
        }
        if (style.getFontFamily() != null) {
            spannedText.setSpan(new TypefaceSpan(style.getFontFamily()), start, end, 33);
        }
        Layout.Alignment textAlign = style.getTextAlign();
        if (textAlign != null) {
            spannedText.setSpan(new AlignmentSpan.Standard(textAlign), start, end, 33);
        }
        switch (style.getFontSizeUnit()) {
            case 1:
                spannedText.setSpan(new AbsoluteSizeSpan((int) style.getFontSize(), true), start, end, 33);
                return;
            case 2:
                spannedText.setSpan(new RelativeSizeSpan(style.getFontSize()), start, end, 33);
                return;
            case 3:
                spannedText.setSpan(new RelativeSizeSpan(style.getFontSize() / 100.0f), start, end, 33);
                return;
            default:
                return;
        }
    }

    private static String getTagName(String tagExpression) {
        String tagExpression2 = tagExpression.trim();
        Assertions.checkArgument(!tagExpression2.isEmpty());
        return Util.splitAtFirst(tagExpression2, "[ \\.]")[0];
    }

    private static void getApplicableStyles(List<WebvttCssStyle> declaredStyles, String id, StartTag tag, List<StyleMatch> output) {
        int styleCount = declaredStyles.size();
        for (int i = 0; i < styleCount; i++) {
            WebvttCssStyle style = declaredStyles.get(i);
            int score = style.getSpecificityScore(id, tag.name, tag.classes, tag.voice);
            if (score > 0) {
                output.add(new StyleMatch(score, style));
            }
        }
        Collections.sort(output);
    }

    /* loaded from: classes3.dex */
    public static final class StyleMatch implements Comparable<StyleMatch> {
        public final int score;
        public final WebvttCssStyle style;

        public StyleMatch(int score, WebvttCssStyle style) {
            this.score = score;
            this.style = style;
        }

        public int compareTo(StyleMatch another) {
            return this.score - another.score;
        }
    }

    /* loaded from: classes3.dex */
    public static final class StartTag {
        private static final String[] NO_CLASSES = new String[0];
        public final String[] classes;
        public final String name;
        public final int position;
        public final String voice;

        private StartTag(String name, int position, String voice, String[] classes) {
            this.position = position;
            this.name = name;
            this.voice = voice;
            this.classes = classes;
        }

        public static StartTag buildStartTag(String fullTagExpression, int position) {
            String voice;
            String[] classes;
            String fullTagExpression2 = fullTagExpression.trim();
            Assertions.checkArgument(!fullTagExpression2.isEmpty());
            int voiceStartIndex = fullTagExpression2.indexOf(" ");
            if (voiceStartIndex == -1) {
                voice = "";
            } else {
                String voice2 = fullTagExpression2.substring(voiceStartIndex);
                voice = voice2.trim();
                fullTagExpression2 = fullTagExpression2.substring(0, voiceStartIndex);
            }
            String[] nameAndClasses = Util.split(fullTagExpression2, "\\.");
            String name = nameAndClasses[0];
            if (nameAndClasses.length > 1) {
                classes = (String[]) Util.nullSafeArrayCopyOfRange(nameAndClasses, 1, nameAndClasses.length);
            } else {
                classes = NO_CLASSES;
            }
            return new StartTag(name, position, voice, classes);
        }

        public static StartTag buildWholeCueVirtualTag() {
            return new StartTag("", 0, "", new String[0]);
        }
    }
}
