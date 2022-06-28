package com.google.android.exoplayer2.text.subrip;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class SubripDecoder extends SimpleSubtitleDecoder {
    private static final String ALIGN_BOTTOM_LEFT = "{\\an1}";
    private static final String ALIGN_BOTTOM_MID = "{\\an2}";
    private static final String ALIGN_BOTTOM_RIGHT = "{\\an3}";
    private static final String ALIGN_MID_LEFT = "{\\an4}";
    private static final String ALIGN_MID_MID = "{\\an5}";
    private static final String ALIGN_MID_RIGHT = "{\\an6}";
    private static final String ALIGN_TOP_LEFT = "{\\an7}";
    private static final String ALIGN_TOP_MID = "{\\an8}";
    private static final String ALIGN_TOP_RIGHT = "{\\an9}";
    private static final float END_FRACTION = 0.92f;
    private static final float MID_FRACTION = 0.5f;
    private static final float START_FRACTION = 0.08f;
    private static final String SUBRIP_ALIGNMENT_TAG = "\\{\\\\an[1-9]\\}";
    private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+)(?:,(\\d+))?";
    private static final String TAG = "SubripDecoder";
    private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("\\s*((?:(\\d+):)?(\\d+):(\\d+)(?:,(\\d+))?)\\s*-->\\s*((?:(\\d+):)?(\\d+):(\\d+)(?:,(\\d+))?)\\s*");
    private static final Pattern SUBRIP_TAG_PATTERN = Pattern.compile("\\{\\\\.*?\\}");
    private final StringBuilder textBuilder = new StringBuilder();
    private final ArrayList<String> tags = new ArrayList<>();

    public SubripDecoder() {
        super(TAG);
    }

    @Override // com.google.android.exoplayer2.text.SimpleSubtitleDecoder
    protected Subtitle decode(byte[] bytes, int length, boolean reset) {
        ArrayList<Cue> cues = new ArrayList<>();
        LongArray cueTimesUs = new LongArray();
        ParsableByteArray subripData = new ParsableByteArray(bytes, length);
        while (true) {
            String currentLine = subripData.readLine();
            if (currentLine == null) {
                break;
            } else if (currentLine.length() != 0) {
                try {
                    Integer.parseInt(currentLine);
                    String currentLine2 = subripData.readLine();
                    if (currentLine2 == null) {
                        Log.w(TAG, "Unexpected end");
                        break;
                    }
                    Matcher matcher = SUBRIP_TIMING_LINE.matcher(currentLine2);
                    if (!matcher.matches()) {
                        Log.w(TAG, "Skipping invalid timing: " + currentLine2);
                    } else {
                        cueTimesUs.add(parseTimecode(matcher, 1));
                        cueTimesUs.add(parseTimecode(matcher, 6));
                        this.textBuilder.setLength(0);
                        this.tags.clear();
                        for (String currentLine3 = subripData.readLine(); !TextUtils.isEmpty(currentLine3); currentLine3 = subripData.readLine()) {
                            if (this.textBuilder.length() > 0) {
                                this.textBuilder.append("<br>");
                            }
                            this.textBuilder.append(processLine(currentLine3, this.tags));
                        }
                        Spanned text = Html.fromHtml(this.textBuilder.toString());
                        String alignmentTag = null;
                        int i = 0;
                        while (true) {
                            if (i >= this.tags.size()) {
                                break;
                            }
                            String tag = this.tags.get(i);
                            if (!tag.matches(SUBRIP_ALIGNMENT_TAG)) {
                                i++;
                            } else {
                                alignmentTag = tag;
                                break;
                            }
                        }
                        cues.add(buildCue(text, alignmentTag));
                        cues.add(Cue.EMPTY);
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Skipping invalid index: " + currentLine);
                }
            }
        }
        Cue[] cuesArray = new Cue[cues.size()];
        cues.toArray(cuesArray);
        long[] cueTimesUsArray = cueTimesUs.toArray();
        return new SubripSubtitle(cuesArray, cueTimesUsArray);
    }

    private String processLine(String line, ArrayList<String> tags) {
        String line2 = line.trim();
        int removedCharacterCount = 0;
        StringBuilder processedLine = new StringBuilder(line2);
        Matcher matcher = SUBRIP_TAG_PATTERN.matcher(line2);
        while (matcher.find()) {
            String tag = matcher.group();
            tags.add(tag);
            int start = matcher.start() - removedCharacterCount;
            int tagLength = tag.length();
            processedLine.replace(start, start + tagLength, "");
            removedCharacterCount += tagLength;
        }
        return processedLine.toString();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private Cue buildCue(Spanned text, String alignmentTag) {
        char c;
        int positionAnchor;
        char c2;
        int lineAnchor;
        if (alignmentTag != null) {
            switch (alignmentTag.hashCode()) {
                case -685620710:
                    if (alignmentTag.equals(ALIGN_BOTTOM_LEFT)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -685620679:
                    if (alignmentTag.equals(ALIGN_BOTTOM_MID)) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case -685620648:
                    if (alignmentTag.equals(ALIGN_BOTTOM_RIGHT)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -685620617:
                    if (alignmentTag.equals(ALIGN_MID_LEFT)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -685620586:
                    if (alignmentTag.equals(ALIGN_MID_MID)) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case -685620555:
                    if (alignmentTag.equals(ALIGN_MID_RIGHT)) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -685620524:
                    if (alignmentTag.equals(ALIGN_TOP_LEFT)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -685620493:
                    if (alignmentTag.equals(ALIGN_TOP_MID)) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case -685620462:
                    if (alignmentTag.equals(ALIGN_TOP_RIGHT)) {
                        c = 5;
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
                    positionAnchor = 0;
                    break;
                case 3:
                case 4:
                case 5:
                    positionAnchor = 2;
                    break;
                default:
                    positionAnchor = 1;
                    break;
            }
            switch (alignmentTag.hashCode()) {
                case -685620710:
                    if (alignmentTag.equals(ALIGN_BOTTOM_LEFT)) {
                        c2 = 0;
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620679:
                    if (alignmentTag.equals(ALIGN_BOTTOM_MID)) {
                        c2 = 1;
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620648:
                    if (alignmentTag.equals(ALIGN_BOTTOM_RIGHT)) {
                        c2 = 2;
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620617:
                    if (alignmentTag.equals(ALIGN_MID_LEFT)) {
                        c2 = 6;
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620586:
                    if (alignmentTag.equals(ALIGN_MID_MID)) {
                        c2 = 7;
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620555:
                    if (alignmentTag.equals(ALIGN_MID_RIGHT)) {
                        c2 = '\b';
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620524:
                    if (alignmentTag.equals(ALIGN_TOP_LEFT)) {
                        c2 = 3;
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620493:
                    if (alignmentTag.equals(ALIGN_TOP_MID)) {
                        c2 = 4;
                        break;
                    }
                    c2 = 65535;
                    break;
                case -685620462:
                    if (alignmentTag.equals(ALIGN_TOP_RIGHT)) {
                        c2 = 5;
                        break;
                    }
                    c2 = 65535;
                    break;
                default:
                    c2 = 65535;
                    break;
            }
            switch (c2) {
                case 0:
                case 1:
                case 2:
                    lineAnchor = 2;
                    break;
                case 3:
                case 4:
                case 5:
                    lineAnchor = 0;
                    break;
                default:
                    lineAnchor = 1;
                    break;
            }
            return new Cue(text, null, getFractionalPositionForAnchorType(lineAnchor), 0, lineAnchor, getFractionalPositionForAnchorType(positionAnchor), positionAnchor, -3.4028235E38f);
        }
        return new Cue(text);
    }

    private static long parseTimecode(Matcher matcher, int groupOffset) {
        String hours = matcher.group(groupOffset + 1);
        long timestampMs = (hours != null ? Long.parseLong(hours) * 60 * 60 * 1000 : 0L) + (Long.parseLong(matcher.group(groupOffset + 2)) * 60 * 1000) + (Long.parseLong(matcher.group(groupOffset + 3)) * 1000);
        String millis = matcher.group(groupOffset + 4);
        if (millis != null) {
            timestampMs += Long.parseLong(millis);
        }
        return 1000 * timestampMs;
    }

    static float getFractionalPositionForAnchorType(int anchorType) {
        switch (anchorType) {
            case 0:
                return START_FRACTION;
            case 1:
                return 0.5f;
            case 2:
                return END_FRACTION;
            default:
                throw new IllegalArgumentException();
        }
    }
}
