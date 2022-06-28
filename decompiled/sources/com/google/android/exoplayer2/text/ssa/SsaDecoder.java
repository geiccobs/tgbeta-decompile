package com.google.android.exoplayer2.text.ssa;

import android.text.Layout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.ssa.SsaStyle;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class SsaDecoder extends SimpleSubtitleDecoder {
    private static final float DEFAULT_MARGIN = 0.05f;
    private static final String DIALOGUE_LINE_PREFIX = "Dialogue:";
    static final String FORMAT_LINE_PREFIX = "Format:";
    private static final Pattern SSA_TIMECODE_PATTERN = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)[:.](\\d+)");
    static final String STYLE_LINE_PREFIX = "Style:";
    private static final String TAG = "SsaDecoder";
    private final SsaDialogueFormat dialogueFormatFromInitializationData;
    private final boolean haveInitializationData;
    private float screenHeight;
    private float screenWidth;
    private Map<String, SsaStyle> styles;

    public SsaDecoder() {
        this(null);
    }

    public SsaDecoder(List<byte[]> initializationData) {
        super(TAG);
        this.screenWidth = -3.4028235E38f;
        this.screenHeight = -3.4028235E38f;
        if (initializationData != null && !initializationData.isEmpty()) {
            this.haveInitializationData = true;
            String formatLine = Util.fromUtf8Bytes(initializationData.get(0));
            Assertions.checkArgument(formatLine.startsWith(FORMAT_LINE_PREFIX));
            this.dialogueFormatFromInitializationData = (SsaDialogueFormat) Assertions.checkNotNull(SsaDialogueFormat.fromFormatLine(formatLine));
            parseHeader(new ParsableByteArray(initializationData.get(1)));
            return;
        }
        this.haveInitializationData = false;
        this.dialogueFormatFromInitializationData = null;
    }

    @Override // com.google.android.exoplayer2.text.SimpleSubtitleDecoder
    protected Subtitle decode(byte[] bytes, int length, boolean reset) {
        List<List<Cue>> cues = new ArrayList<>();
        List<Long> cueTimesUs = new ArrayList<>();
        ParsableByteArray data = new ParsableByteArray(bytes, length);
        if (!this.haveInitializationData) {
            parseHeader(data);
        }
        parseEventBody(data, cues, cueTimesUs);
        return new SsaSubtitle(cues, cueTimesUs);
    }

    private void parseHeader(ParsableByteArray data) {
        while (true) {
            String currentLine = data.readLine();
            if (currentLine != null) {
                if ("[Script Info]".equalsIgnoreCase(currentLine)) {
                    parseScriptInfo(data);
                } else if ("[V4+ Styles]".equalsIgnoreCase(currentLine)) {
                    this.styles = parseStyles(data);
                } else if ("[V4 Styles]".equalsIgnoreCase(currentLine)) {
                    Log.i(TAG, "[V4 Styles] are not supported");
                } else if ("[Events]".equalsIgnoreCase(currentLine)) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0045, code lost:
        if (r3.equals("playresx") != false) goto L18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void parseScriptInfo(com.google.android.exoplayer2.util.ParsableByteArray r8) {
        /*
            r7 = this;
        L0:
            java.lang.String r0 = r8.readLine()
            r1 = r0
            if (r0 == 0) goto L6c
            int r0 = r8.bytesLeft()
            if (r0 == 0) goto L15
            int r0 = r8.peekUnsignedByte()
            r2 = 91
            if (r0 == r2) goto L6c
        L15:
            java.lang.String r0 = ":"
            java.lang.String[] r0 = r1.split(r0)
            int r2 = r0.length
            r3 = 2
            if (r2 == r3) goto L20
            goto L0
        L20:
            r2 = 0
            r3 = r0[r2]
            java.lang.String r3 = r3.trim()
            java.lang.String r3 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r3)
            r4 = -1
            int r5 = r3.hashCode()
            r6 = 1
            switch(r5) {
                case 1879649548: goto L3f;
                case 1879649549: goto L35;
                default: goto L34;
            }
        L34:
            goto L48
        L35:
            java.lang.String r2 = "playresy"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L34
            r2 = 1
            goto L49
        L3f:
            java.lang.String r5 = "playresx"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L34
            goto L49
        L48:
            r2 = -1
        L49:
            switch(r2) {
                case 0: goto L5c;
                case 1: goto L4d;
                default: goto L4c;
            }
        L4c:
            goto L6b
        L4d:
            r2 = r0[r6]     // Catch: java.lang.NumberFormatException -> L5a
            java.lang.String r2 = r2.trim()     // Catch: java.lang.NumberFormatException -> L5a
            float r2 = java.lang.Float.parseFloat(r2)     // Catch: java.lang.NumberFormatException -> L5a
            r7.screenHeight = r2     // Catch: java.lang.NumberFormatException -> L5a
            goto L6b
        L5a:
            r2 = move-exception
            goto L6b
        L5c:
            r2 = r0[r6]     // Catch: java.lang.NumberFormatException -> L69
            java.lang.String r2 = r2.trim()     // Catch: java.lang.NumberFormatException -> L69
            float r2 = java.lang.Float.parseFloat(r2)     // Catch: java.lang.NumberFormatException -> L69
            r7.screenWidth = r2     // Catch: java.lang.NumberFormatException -> L69
            goto L6b
        L69:
            r2 = move-exception
        L6b:
            goto L0
        L6c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ssa.SsaDecoder.parseScriptInfo(com.google.android.exoplayer2.util.ParsableByteArray):void");
    }

    private static Map<String, SsaStyle> parseStyles(ParsableByteArray data) {
        Map<String, SsaStyle> styles = new LinkedHashMap<>();
        SsaStyle.Format formatInfo = null;
        while (true) {
            String currentLine = data.readLine();
            if (currentLine == null || (data.bytesLeft() != 0 && data.peekUnsignedByte() == 91)) {
                break;
            } else if (currentLine.startsWith(FORMAT_LINE_PREFIX)) {
                formatInfo = SsaStyle.Format.fromFormatLine(currentLine);
            } else if (currentLine.startsWith(STYLE_LINE_PREFIX)) {
                if (formatInfo == null) {
                    Log.w(TAG, "Skipping 'Style:' line before 'Format:' line: " + currentLine);
                } else {
                    SsaStyle style = SsaStyle.fromStyleLine(currentLine, formatInfo);
                    if (style != null) {
                        styles.put(style.name, style);
                    }
                }
            }
        }
        return styles;
    }

    private void parseEventBody(ParsableByteArray data, List<List<Cue>> cues, List<Long> cueTimesUs) {
        SsaDialogueFormat format = this.haveInitializationData ? this.dialogueFormatFromInitializationData : null;
        while (true) {
            String currentLine = data.readLine();
            if (currentLine != null) {
                if (currentLine.startsWith(FORMAT_LINE_PREFIX)) {
                    format = SsaDialogueFormat.fromFormatLine(currentLine);
                } else if (currentLine.startsWith(DIALOGUE_LINE_PREFIX)) {
                    if (format == null) {
                        Log.w(TAG, "Skipping dialogue line before complete format: " + currentLine);
                    } else {
                        parseDialogueLine(currentLine, format, cues, cueTimesUs);
                    }
                }
            } else {
                return;
            }
        }
    }

    private void parseDialogueLine(String dialogueLine, SsaDialogueFormat format, List<List<Cue>> cues, List<Long> cueTimesUs) {
        SsaStyle style;
        Assertions.checkArgument(dialogueLine.startsWith(DIALOGUE_LINE_PREFIX));
        String[] lineValues = dialogueLine.substring(DIALOGUE_LINE_PREFIX.length()).split(",", format.length);
        if (lineValues.length != format.length) {
            Log.w(TAG, "Skipping dialogue line with fewer columns than format: " + dialogueLine);
            return;
        }
        long startTimeUs = parseTimecodeUs(lineValues[format.startTimeIndex]);
        if (startTimeUs == C.TIME_UNSET) {
            Log.w(TAG, "Skipping invalid timing: " + dialogueLine);
            return;
        }
        long endTimeUs = parseTimecodeUs(lineValues[format.endTimeIndex]);
        if (endTimeUs == C.TIME_UNSET) {
            Log.w(TAG, "Skipping invalid timing: " + dialogueLine);
            return;
        }
        if (this.styles != null && format.styleIndex != -1) {
            style = this.styles.get(lineValues[format.styleIndex].trim());
        } else {
            style = null;
        }
        String rawText = lineValues[format.textIndex];
        SsaStyle.Overrides styleOverrides = SsaStyle.Overrides.parseFromDialogue(rawText);
        String text = SsaStyle.Overrides.stripStyleOverrides(rawText).replaceAll("\\\\N", "\n").replaceAll("\\\\n", "\n");
        Cue cue = createCue(text, style, styleOverrides, this.screenWidth, this.screenHeight);
        int startTimeIndex = addCuePlacerholderByTime(startTimeUs, cueTimesUs, cues);
        int i = startTimeIndex;
        for (int endTimeIndex = addCuePlacerholderByTime(endTimeUs, cueTimesUs, cues); i < endTimeIndex; endTimeIndex = endTimeIndex) {
            cues.get(i).add(cue);
            i++;
        }
    }

    private static long parseTimecodeUs(String timeString) {
        Matcher matcher = SSA_TIMECODE_PATTERN.matcher(timeString.trim());
        if (!matcher.matches()) {
            return C.TIME_UNSET;
        }
        long timestampUs = Long.parseLong((String) Util.castNonNull(matcher.group(1))) * 60 * 60 * 1000000;
        return timestampUs + (Long.parseLong((String) Util.castNonNull(matcher.group(2))) * 60 * 1000000) + (Long.parseLong((String) Util.castNonNull(matcher.group(3))) * 1000000) + (Long.parseLong((String) Util.castNonNull(matcher.group(4))) * 10000);
    }

    private static Cue createCue(String text, SsaStyle style, SsaStyle.Overrides styleOverrides, float screenWidth, float screenHeight) {
        int alignment;
        float line;
        float position;
        if (styleOverrides.alignment != -1) {
            alignment = styleOverrides.alignment;
        } else if (style != null) {
            alignment = style.alignment;
        } else {
            alignment = -1;
        }
        int positionAnchor = toPositionAnchor(alignment);
        int lineAnchor = toLineAnchor(alignment);
        if (styleOverrides.position != null && screenHeight != -3.4028235E38f && screenWidth != -3.4028235E38f) {
            float position2 = styleOverrides.position.x / screenWidth;
            position = position2;
            line = styleOverrides.position.y / screenHeight;
        } else {
            float position3 = computeDefaultLineOrPosition(positionAnchor);
            position = position3;
            line = computeDefaultLineOrPosition(lineAnchor);
        }
        return new Cue(text, toTextAlignment(alignment), line, 0, lineAnchor, position, positionAnchor, -3.4028235E38f);
    }

    private static Layout.Alignment toTextAlignment(int alignment) {
        switch (alignment) {
            case -1:
                return null;
            case 0:
            default:
                Log.w(TAG, "Unknown alignment: " + alignment);
                return null;
            case 1:
            case 4:
            case 7:
                return Layout.Alignment.ALIGN_NORMAL;
            case 2:
            case 5:
            case 8:
                return Layout.Alignment.ALIGN_CENTER;
            case 3:
            case 6:
            case 9:
                return Layout.Alignment.ALIGN_OPPOSITE;
        }
    }

    private static int toLineAnchor(int alignment) {
        switch (alignment) {
            case -1:
                return Integer.MIN_VALUE;
            case 0:
            default:
                Log.w(TAG, "Unknown alignment: " + alignment);
                return Integer.MIN_VALUE;
            case 1:
            case 2:
            case 3:
                return 2;
            case 4:
            case 5:
            case 6:
                return 1;
            case 7:
            case 8:
            case 9:
                return 0;
        }
    }

    private static int toPositionAnchor(int alignment) {
        switch (alignment) {
            case -1:
                return Integer.MIN_VALUE;
            case 0:
            default:
                Log.w(TAG, "Unknown alignment: " + alignment);
                return Integer.MIN_VALUE;
            case 1:
            case 4:
            case 7:
                return 0;
            case 2:
            case 5:
            case 8:
                return 1;
            case 3:
            case 6:
            case 9:
                return 2;
        }
    }

    private static float computeDefaultLineOrPosition(int anchor) {
        switch (anchor) {
            case 0:
                return DEFAULT_MARGIN;
            case 1:
                return 0.5f;
            case 2:
                return 0.95f;
            default:
                return -3.4028235E38f;
        }
    }

    private static int addCuePlacerholderByTime(long timeUs, List<Long> sortedCueTimesUs, List<List<Cue>> cues) {
        int insertionIndex = 0;
        int i = sortedCueTimesUs.size() - 1;
        while (true) {
            if (i < 0) {
                break;
            } else if (sortedCueTimesUs.get(i).longValue() == timeUs) {
                return i;
            } else {
                if (sortedCueTimesUs.get(i).longValue() >= timeUs) {
                    i--;
                } else {
                    insertionIndex = i + 1;
                    break;
                }
            }
        }
        sortedCueTimesUs.add(insertionIndex, Long.valueOf(timeUs));
        cues.add(insertionIndex, insertionIndex == 0 ? new ArrayList() : new ArrayList(cues.get(insertionIndex - 1)));
        return insertionIndex;
    }
}
