package com.google.android.exoplayer2.text.ssa;

import android.graphics.PointF;
import android.text.TextUtils;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class SsaStyle {
    public static final int SSA_ALIGNMENT_BOTTOM_CENTER = 2;
    public static final int SSA_ALIGNMENT_BOTTOM_LEFT = 1;
    public static final int SSA_ALIGNMENT_BOTTOM_RIGHT = 3;
    public static final int SSA_ALIGNMENT_MIDDLE_CENTER = 5;
    public static final int SSA_ALIGNMENT_MIDDLE_LEFT = 4;
    public static final int SSA_ALIGNMENT_MIDDLE_RIGHT = 6;
    public static final int SSA_ALIGNMENT_TOP_CENTER = 8;
    public static final int SSA_ALIGNMENT_TOP_LEFT = 7;
    public static final int SSA_ALIGNMENT_TOP_RIGHT = 9;
    public static final int SSA_ALIGNMENT_UNKNOWN = -1;
    private static final String TAG = "SsaStyle";
    public final int alignment;
    public final String name;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SsaAlignment {
    }

    private SsaStyle(String name, int alignment) {
        this.name = name;
        this.alignment = alignment;
    }

    public static SsaStyle fromStyleLine(String styleLine, Format format) {
        Assertions.checkArgument(styleLine.startsWith("Style:"));
        String[] styleValues = TextUtils.split(styleLine.substring("Style:".length()), ",");
        if (styleValues.length != format.length) {
            Log.w(TAG, Util.formatInvariant("Skipping malformed 'Style:' line (expected %s values, found %s): '%s'", Integer.valueOf(format.length), Integer.valueOf(styleValues.length), styleLine));
            return null;
        }
        try {
            return new SsaStyle(styleValues[format.nameIndex].trim(), parseAlignment(styleValues[format.alignmentIndex]));
        } catch (RuntimeException e) {
            Log.w(TAG, "Skipping malformed 'Style:' line: '" + styleLine + "'", e);
            return null;
        }
    }

    public static int parseAlignment(String alignmentStr) {
        try {
            int alignment = Integer.parseInt(alignmentStr.trim());
            if (isValidAlignment(alignment)) {
                return alignment;
            }
        } catch (NumberFormatException e) {
        }
        Log.w(TAG, "Ignoring unknown alignment: " + alignmentStr);
        return -1;
    }

    private static boolean isValidAlignment(int alignment) {
        switch (alignment) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class Format {
        public final int alignmentIndex;
        public final int length;
        public final int nameIndex;

        private Format(int nameIndex, int alignmentIndex, int length) {
            this.nameIndex = nameIndex;
            this.alignmentIndex = alignmentIndex;
            this.length = length;
        }

        public static Format fromFormatLine(String styleFormatLine) {
            int nameIndex = -1;
            int alignmentIndex = -1;
            String[] keys = TextUtils.split(styleFormatLine.substring("Format:".length()), ",");
            int i = 0;
            while (true) {
                char c = 65535;
                if (i < keys.length) {
                    String lowerInvariant = Util.toLowerInvariant(keys[i].trim());
                    switch (lowerInvariant.hashCode()) {
                        case 3373707:
                            if (lowerInvariant.equals(CommonProperties.NAME)) {
                                c = 0;
                                break;
                            }
                            break;
                        case 1767875043:
                            if (lowerInvariant.equals("alignment")) {
                                c = 1;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            nameIndex = i;
                            break;
                        case 1:
                            alignmentIndex = i;
                            break;
                    }
                    i++;
                } else if (nameIndex == -1) {
                    return null;
                } else {
                    return new Format(nameIndex, alignmentIndex, keys.length);
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    static final class Overrides {
        private static final String TAG = "SsaStyle.Overrides";
        public final int alignment;
        public final PointF position;
        private static final Pattern BRACES_PATTERN = Pattern.compile("\\{([^}]*)\\}");
        private static final String PADDED_DECIMAL_PATTERN = "\\s*\\d+(?:\\.\\d+)?\\s*";
        private static final Pattern POSITION_PATTERN = Pattern.compile(Util.formatInvariant("\\\\pos\\((%1$s),(%1$s)\\)", PADDED_DECIMAL_PATTERN));
        private static final Pattern MOVE_PATTERN = Pattern.compile(Util.formatInvariant("\\\\move\\(%1$s,%1$s,(%1$s),(%1$s)(?:,%1$s,%1$s)?\\)", PADDED_DECIMAL_PATTERN));
        private static final Pattern ALIGNMENT_OVERRIDE_PATTERN = Pattern.compile("\\\\an(\\d+)");

        private Overrides(int alignment, PointF position) {
            this.alignment = alignment;
            this.position = position;
        }

        public static Overrides parseFromDialogue(String text) {
            int alignment = -1;
            PointF position = null;
            Matcher matcher = BRACES_PATTERN.matcher(text);
            while (matcher.find()) {
                String braceContents = matcher.group(1);
                try {
                    PointF parsedPosition = parsePosition(braceContents);
                    if (parsedPosition != null) {
                        position = parsedPosition;
                    }
                } catch (RuntimeException e) {
                }
                try {
                    int parsedAlignment = parseAlignmentOverride(braceContents);
                    if (parsedAlignment != -1) {
                        alignment = parsedAlignment;
                    }
                } catch (RuntimeException e2) {
                }
            }
            return new Overrides(alignment, position);
        }

        public static String stripStyleOverrides(String dialogueLine) {
            return BRACES_PATTERN.matcher(dialogueLine).replaceAll("");
        }

        private static PointF parsePosition(String styleOverride) {
            String x;
            String y;
            Matcher positionMatcher = POSITION_PATTERN.matcher(styleOverride);
            Matcher moveMatcher = MOVE_PATTERN.matcher(styleOverride);
            boolean hasPosition = positionMatcher.find();
            boolean hasMove = moveMatcher.find();
            if (hasPosition) {
                if (hasMove) {
                    Log.i(TAG, "Override has both \\pos(x,y) and \\move(x1,y1,x2,y2); using \\pos values. override='" + styleOverride + "'");
                }
                x = positionMatcher.group(1);
                y = positionMatcher.group(2);
            } else if (hasMove) {
                x = moveMatcher.group(1);
                y = moveMatcher.group(2);
            } else {
                return null;
            }
            return new PointF(Float.parseFloat(((String) Assertions.checkNotNull(x)).trim()), Float.parseFloat(((String) Assertions.checkNotNull(y)).trim()));
        }

        private static int parseAlignmentOverride(String braceContents) {
            Matcher matcher = ALIGNMENT_OVERRIDE_PATTERN.matcher(braceContents);
            if (matcher.find()) {
                return SsaStyle.parseAlignment(matcher.group(1));
            }
            return -1;
        }
    }
}
