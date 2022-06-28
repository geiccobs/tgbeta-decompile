package com.google.android.exoplayer2.text.webvtt;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.microsoft.appcenter.Constants;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class WebvttParserUtil {
    private static final Pattern COMMENT = Pattern.compile("^NOTE([ \t].*)?$");
    private static final String WEBVTT_HEADER = "WEBVTT";

    private WebvttParserUtil() {
    }

    public static void validateWebvttHeaderLine(ParsableByteArray input) throws ParserException {
        int startPosition = input.getPosition();
        if (!isWebvttHeaderLine(input)) {
            input.setPosition(startPosition);
            throw new ParserException("Expected WEBVTT. Got " + input.readLine());
        }
    }

    public static boolean isWebvttHeaderLine(ParsableByteArray input) {
        String line = input.readLine();
        return line != null && line.startsWith(WEBVTT_HEADER);
    }

    public static long parseTimestampUs(String timestamp) throws NumberFormatException {
        long value = 0;
        String[] parts = Util.splitAtFirst(timestamp, "\\.");
        String[] subparts = Util.split(parts[0], Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        for (String subpart : subparts) {
            value = (60 * value) + Long.parseLong(subpart);
        }
        long value2 = value * 1000;
        if (parts.length == 2) {
            value2 += Long.parseLong(parts[1]);
        }
        return 1000 * value2;
    }

    public static float parsePercentage(String s) throws NumberFormatException {
        if (!s.endsWith("%")) {
            throw new NumberFormatException("Percentages must end with %");
        }
        return Float.parseFloat(s.substring(0, s.length() - 1)) / 100.0f;
    }

    public static Matcher findNextCueHeader(ParsableByteArray input) {
        String line;
        while (true) {
            String line2 = input.readLine();
            if (line2 != null) {
                if (COMMENT.matcher(line2).matches()) {
                    do {
                        line = input.readLine();
                        if (line != null) {
                        }
                    } while (!line.isEmpty());
                } else {
                    Matcher cueHeaderMatcher = WebvttCueParser.CUE_HEADER_PATTERN.matcher(line2);
                    if (cueHeaderMatcher.matches()) {
                        return cueHeaderMatcher;
                    }
                }
            } else {
                return null;
            }
        }
    }
}
